package com.taoshao.taobi.bizmq;

import com.rabbitmq.client.Channel;
import com.taoshao.taobi.common.ErrorCode;
import com.taoshao.taobi.constant.BiMqConstant;
import com.taoshao.taobi.exception.BusinessException;
import com.taoshao.taobi.manager.AiManager;
import com.taoshao.taobi.model.entity.Chart;
import com.taoshao.taobi.model.enums.ChartStatusEnum;
import com.taoshao.taobi.service.ChartService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.io.IOException;

import static com.taoshao.taobi.constant.CommonConstant.BI_MODEL_ID;


/**
 * @Author taoshao
 * @Date 2024/6/29
 */
@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    /**
     * 指定程序监听的消息队列和确认机制
     */
    @SneakyThrows(Exception.class)
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage {}",message);
        if (StringUtils.isBlank(message)) {
            //如果失败，消息拒绝
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }
        //先修改图表任务状态 ”运行中“，等待执行成功后，修改为 ”已完成“，保存执行结果，执行失败后，状态改为 ”失败“，记录任务失败信息
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus(ChartStatusEnum.RUNNING.getValue());
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表运行中状态失败");
            return;
        }
        //调用 AI
        String result = aiManager.doChat(BI_MODEL_ID, buildUserInput(chart));
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError(chart.getId(), "AI 生成错误");
            return;
        }
        //.trim(),用于移除字符串两端的空白字符。空白字符包括空格、制表符、换行符等。
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();

        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        updateChartResult.setStatus(ChartStatusEnum.SUCCEED.getValue());
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
        }
        //消息确认
        channel.basicAck(deliveryTag, false);
    }


    /**
     * 消费异常消息
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows(Exception.class)
    @RabbitListener(queues = {BiMqConstant.BI_DLX_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveErrorMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if (StringUtils.isBlank(message)){
            //如果失败，消息拒绝
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }
        handleChartUpdateError(chartId,"更新图表失败");
        try {
            channel.basicAck(deliveryTag,false);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }


    /**
     * 构建用户输入
     *
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();
        //用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        //拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }


    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus(ChartStatusEnum.FAILED.getValue());
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }


}
