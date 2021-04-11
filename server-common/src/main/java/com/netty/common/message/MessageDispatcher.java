package com.netty.common.message;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wy
 * @Description 将 Invocation 分发到其对应的 MessageHandler 中，进行业务逻辑的执行。代码如下：
 * ① 在类上添加 @ChannelHandler.Sharable 注解，标记这个 ChannelHandler 可以被多个 Channel 使用。
 * 2 SimpleChannelInboundHandler 是 Netty 定义的消息处理 ChannelHandler 抽象类，处理消息的类型是 <I> 泛型时
 * @createTime 2021/03/14
 */
@ChannelHandler.Sharable
@Component
public class MessageDispatcher extends SimpleChannelInboundHandler<Invocation> {

    @Resource
    private MessageHandlerContainer messageHandlerContainer;

    private final ExecutorService executor = Executors.newFixedThreadPool(200);

    /**
     * 处理消息，进行分发。
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Invocation msg) throws Exception {
        // <3.1> 获得 type 对应的 MessageHandler 处理器
        // 调用 MessageHandlerContainer 的 #getMessageHandler(String type) 方法，
        // 获得 Invocation 的 type 对应的 MessageHandler 处理器
        MessageHandler messageHandler = messageHandlerContainer.getMessageHandler(msg.getType());
        // 调用 MessageHandlerContainer 的 #getMessageClass(messageHandler) 方法，
        // 获得 MessageHandler 处理器的消息类。
        Class<? extends Message> messageClass = MessageHandlerContainer.getMessageClass(messageHandler);
        // <3.2> 解析消息 ，将 Invocation 的 message 解析成 MessageHandler 对应的消息对象
        Message message = JSON.parseObject(msg.getMessage(), messageClass);
        /*
        *   <3.3> 执行逻辑 ，丢到线程池中，然后调用 MessageHandler
         的 #execute(Channel channel, T message) 方法，执行业务逻辑。
           为什么要丢到 executor 线程池中呢？我们先来了解下 EventGroup 的线程模型
         EventGroup 我们可以先简单理解成一个线程池，并且线程池的大小仅仅是 CPU 数量 * 2。
          每个 Channel 仅仅会被分配到其中的一个线程上，进行数据的读写。
         并且，多个 Channel 会共享一个线程，即使用同一个线程进行数据的读写
          MessageHandler 的具体逻辑视线中，往往会涉及到 IO 处理，例如说进行数据库的读取。
         这样，就会导致一个 Channel 在执行 MessageHandler 的过程中，阻塞了共享当前线程的其它 Channel 的数据读取。
         因此，我们在这里创建了 executor 线程池，进行 MessageHandler 的逻辑执行，避免阻塞 Channel 的数据读取。
        */
        executor.submit(() -> {
            // noinspection unchecked
            messageHandler.execute(ctx.channel(), message);
        });
    }
}
