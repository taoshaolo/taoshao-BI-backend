package com.taoshao.taobi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class FanoutConsumer {
    private static final String EXCHANGE_NAME = "fanout_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        Channel channel2 = connection.createChannel();

        // 声名交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // 创建队列
        String queueName = "小王的工作队列";
        //声名队列
        channel.queueDeclare(queueName, true, false, false, null);
        //绑定交换机
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        // 创建队列
        String queueName2 = "小李的工作队列";
        //声名队列
        channel2.queueDeclare(queueName2, true, false, false, null);
        //绑定交换机
        channel2.queueBind(queueName2, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小王] Received '" + message + "'");
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小李] Received '" + message + "'");
        };
        //监听
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
        channel2.basicConsume(queueName2, true, deliverCallback2, consumerTag -> {
        });
    }
}