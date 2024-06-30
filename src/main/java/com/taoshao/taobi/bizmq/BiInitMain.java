package com.taoshao.taobi.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

import static com.taoshao.taobi.constant.BiMqConstant.*;

/**
 * 用于创建多的是程序用到的交换机和队列（只有执行一次）
 *
 * @Author taoshao
 * @Date 2024/6/29
 */
public class BiInitMain {
    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            //声名死信 交换机和队列
            channel.exchangeDeclare(BI_DLX_EXCHANGE_NAME, "direct");
            channel.queueDeclare(BI_DLX_QUEUE_NAME, true, false, false, null);
            channel.queueBind(BI_DLX_QUEUE_NAME, BI_DLX_EXCHANGE_NAME, BI_DLX_ROUTING_KEY);

            //声名交换机
            channel.exchangeDeclare(BI_EXCHANGE_NAME, "direct");

            // 指定死信队列参数
            Map<String, Object> arg = new HashMap<>();
            // 要绑定到哪个交换机
            arg.put("x-dead-letter-exchange", BI_DLX_EXCHANGE_NAME);
            // 指定死信要转发到哪个死信队列
            arg.put("x-dead-letter-routing-key", BI_DLX_ROUTING_KEY);

            //创建队列
            channel.queueDeclare(BI_QUEUE_NAME, true, false, false, arg);
            channel.queueBind(BI_QUEUE_NAME, BI_EXCHANGE_NAME, BI_ROUTING_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
