package com.taoshao.taobi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

public class DlxDirectConsumer {

    private static final String DEAD_EXCHANGE_NAME = "dlx_direct_exchange";

    private static final String WORK_EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(WORK_EXCHANGE_NAME, "direct");

        //指定死信队列参数
        Map<String, Object> args = new HashMap<>();
        //要绑定到哪个交换机
        args.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        //指定死信要转发到哪个死信队列
        args.put("x-dead-letter-routing-key", "waibao");

        //创建队列
        String queueName = "张三的工作队列";
        channel.queueDeclare(queueName,true,false,false,args);
        channel.queueBind(queueName,WORK_EXCHANGE_NAME,"zs");

        //指定死信队列参数
        Map<String, Object> args2 = new HashMap<>();
        //要绑定到哪个交换机
        args2.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        //指定死信要转发到哪个死信队列
        args2.put("x-dead-letter-routing-key", "laoban");

        //创建队列
        String queueName2 = "李四的工作队列";
        channel.queueDeclare(queueName2,true,false,false,args2);
        channel.queueBind(queueName2,WORK_EXCHANGE_NAME,"ls");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            //拒绝消息
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
            System.out.println(" [张三] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            //拒绝消息
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
            System.out.println(" [李四] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
        });

        channel.basicConsume(queueName2, false, deliverCallback2, consumerTag -> {
        });


    }
}