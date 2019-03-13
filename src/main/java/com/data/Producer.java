package com.data;

import com.data.pojo.User;
import com.data.util.ProtoStuffSerializerUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.util.HashMap;

/**
 * 发送端
 */
public class Producer {


    private static String queueName = "queue2";
    public static void main(String[] args) throws Exception{

        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("guest");
        factory.setPassword("guest");

        //创建一个新的连接
        Connection connection = factory.newConnection();

        //创建一个通道
        Channel channel = connection.createChannel();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("x-message-ttl", 10000);
        /**
         * 声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
         * var1：表示队列名称、
         * var2：为是否持久化（true表示是，队列将在服务器重启时生存）、
         * var3：为是否是独占队列（创建者可以使用的私有队列，断开后自动删除）、
         * var4：为当所有消费者客户端连接断开时是否自动删除队列、
         * var5：为队列的其他参数
         *      Message TTL(x-message-ttl)：设置队列中的所有消息的生存周期(统一为整个队列的所有消息设置生命周期), 也可以在发布消息的时候单独为某个消息指定剩余生存时间,单位毫秒, 类似于redis中的ttl，生存时间到了，消息会被从队里中删除，注意是消息被删除，而不是队列被删除， 特性Features=TTL, 单独为某条消息设置过期时间AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties().builder().expiration(“6000”);
         *      channel.basicPublish(EXCHANGE_NAME, “”, properties.build(), message.getBytes(“UTF-8”));
         *      Auto Expire(x-expires): 当队列在指定的时间没有被访问(consume, basicGet, queueDeclare…)就会被删除,Features=Exp
         *      Max Length(x-max-length): 限定队列的消息的最大值长度，超过指定长度将会把最早的几条删除掉， 类似于mongodb中的固定集合，例如保存最新的100条消息, Feature=Lim
         *      Max Length Bytes(x-max-length-bytes): 限定队列最大占用的空间大小， 一般受限于内存、磁盘的大小, Features=Lim B
         *      Dead letter exchange(x-dead-letter-exchange)： 当队列消息长度大于最大长度、或者过期的等，将从队列中删除的消息推送到指定的交换机中去而不是丢弃掉,Features=DLX
         *      Dead letter routing key(x-dead-letter-routing-key)：将删除的消息推送到指定交换机的指定路由键的队列中去, Feature=DLK
         *      Maximum priority(x-max-priority)：优先级队列，声明队列时先定义最大优先级值(定义最大值一般不要太大)，在发布消息的时候指定该消息的优先级， 优先级更高（数值更大的）的消息先被消费,
         *      Lazy mode(x-queue-mode=lazy)： Lazy Queues: 先将消息保存到磁盘上，不放在内存中，当消费者开始消费的时候才加载到内存中
         *      Master locator(x-queue-master-locator)
         */
        channel.queueDeclare(queueName, true, false, false, map);

        User user = new User();
        user.setUserId(1);
        user.setName("卢春");
        user.setSex("女");
        for (int i = 0; i < 20; i++) {
            //发送的消息
            String message = "hello world!"+i;
            //往队列中发出一条消息
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, ProtoStuffSerializerUtil.serialize(user));
            System.out.println(" [x] Sent '" + message + "'");
//            Thread.sleep(1000);
        }
        //关闭频道和连接
        channel.close();
        connection.close();
    }
}