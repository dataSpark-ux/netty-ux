package com.wy.collect.collect.t808;

import com.wy.core.protocol.T808Message;
import com.wy.core.queue.MsgQueueFactory;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 解析成T808Message消息 - 并将消息派发
 *
 * <p>
 * SimpleChannelInboundHandler的channelRead0还有一个好处就是你不用关心释放资源，因为源码中已经帮你释放了，所以如果你保存获取的信息的引用，是无效的
 * -> ProtocolDecoder 重新创建808消息对象
 * @author wangyi
 */
@Sharable
@Slf4j
@Component
public class T808ChannelHandler extends SimpleChannelInboundHandler<T808Message> {

    @Autowired
    T808ConnectionManager gpsConnectionManager;

    @Autowired
    MsgQueueFactory msgQueueFactory;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 读取数据 ， 客户端向服务端发来数据，每次都会回调此方法，表示有数据可读
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T808Message tm) throws Exception {
        // 更新连接状态, 并更新在线时间和在线状态[在线]
        gpsConnectionManager.connection(tm.getOnlineNo(), ctx.channel());

        // 发消息出去
        msgQueueFactory.send(tm);
    }

    /**
     * Netty TCP 连接异常 , 及连接的释放流程 : exceptionCaught -> channelInactive -> channelUnregistered
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    /**
     * handlerAdded() -> channelRegistered() -> channelActive() -> channelRead() -> channelReadComplete()
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(String.format("==> 连接开启 [%s]", ctx.channel()
                                                        .remoteAddress()));
        }
    }

    /**
     * TCP 的建立
     * channel 的所有的业务逻辑链准备完毕（也就是说 channel 的 pipeline 中已经添加完所有的 handler）以及绑定好一个 NIO 线程之后，这条连接算是真正激活了，接下来就会回调到此方法。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(String.format("==> 连接开启 [%s]", ctx.channel()
                                                        .remoteAddress()));
        }
    }

    /**
     * TCP的释放，表明这条连接已经被关闭了，这条连接在 TCP 层面已经不再是 ESTABLISH 状态了
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(String.format("<== 连接关闭 [%s]", ctx.channel()
                                                        .remoteAddress()));
        }
        gpsConnectionManager.disconnection(ctx.channel());
    }

    /**
     * 表明与这条连接对应的 NIO 线程移除掉对这条连接的处理
     * channelInactive() -> channelUnregistered() -> handlerRemoved()
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

    }

    /**
     * 事件回调
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // 读超时 => 关闭连接
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
