package com.taoshao.taobi.bizmq;

import com.taoshao.taobi.constant.BiMqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author taoshao
 * @Date 2024/6/29
 */
@Component
public class BiMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage( String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY, message);
    }
}
