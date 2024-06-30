package com.taoshao.taobi.mq;

import com.rabbitmq.client.*;

public class DirectConsumer {

    private static final String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        //创建队列
        String queueName = "小高的工作队列";
        channel.queueDeclare(queueName,true,false,false,null);
        channel.queueBind(queueName,EXCHANGE_NAME,"xiaogao");

        //创建队列
        String queueName2 = "小涛的工作队列";
        channel.queueDeclare(queueName2,true,false,false,null);
        channel.queueBind(queueName2,EXCHANGE_NAME,"xiaotao");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小高] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小涛] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });

        channel.basicConsume(queueName2, true, deliverCallback2, consumerTag -> {
        });
    }
}