package com.data;


import com.data.pojo.User;
import com.data.util.ProtoStuffSerializerUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.tools.json.JSONUtil;
import net.sf.json.JSONObject;
import netscape.javascript.JSObject;

/**
 * 接收端
 */
public class Consumer {

    private static String queueName = "queue2";

    public static void main(String[] args) throws Exception {

        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("guest");
        factory.setPassword("guest");

        //创建一个新的连接
        Connection connection = factory.newConnection();

        //创建一个通道
        Channel channel = connection.createChannel();

        /**
         * 声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
         * var1：表示队列名称、
         * var2：为是否持久化（true表示是，队列将在服务器重启时生存）、
         * var3：为是否是独占队列（创建者可以使用的私有队列，断开后自动删除）、
         * var4：为当所有消费者客户端连接断开时是否自动删除队列、
         * var5：为队列的其他参数
         */
        channel.queueDeclare(queueName, true, false, false, null);
        System.out.println(Consumer.class.hashCode() + " [*] Waiting for messages. To exit press CTRL+C");

        // 创建队列消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);

        // 设置最大服务消息接收数量
        int prefetchCount = 1;
        channel.basicQos(prefetchCount);

        boolean ack = false; // 是否自动确认消息被成功消费
        channel.basicConsume(queueName, ack, consumer); // 指定消费队列

        while (true) {
            // nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            User user = ProtoStuffSerializerUtil.deserialize(delivery.getBody(), User.class);
            //System.out.println(JSONObject.fromObject(user));
            String message = new String(delivery.getBody());
            //System.out.println(" [x] Received '" +  message + "'");

            //手动发送，告诉消息服务器来删除消息
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            Thread.sleep(2000);
        }

    }

}