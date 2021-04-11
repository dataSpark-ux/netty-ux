package com.netty.app.server;

import com.netty.app.server.handler.NettyServerHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sound.midi.Track;
import java.net.InetSocketAddress;

/**
 * @author wy
 * @Description netty初始化
 * @createTime 2021/03/14
 */
@Component
@Slf4j
public class NettyServer {

    private Integer port = 9099;

    @Resource
    private NettyServerHandlerInitializer nettyServerHandlerInitializer;

    /**
     * boss 线程组 用于服务端接受客户端的连接
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    /**
     * worker 线程组 用于服务端接受客户端的读写
     */
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    /**
     * Netty Server Channel
     */
    private Channel channel;

    @PostConstruct
    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap
                // 设置两个 EventLoopGroup 对象
                .group(bossGroup, workerGroup)
                // 指定 Channel 为服务端 NioServerSocketChannel
                .channel(NioServerSocketChannel.class)
                // 设置 Netty Server 的端口
                .localAddress(new InetSocketAddress(port))
                // 设置服务端接受客户端的连接队列大小。
                // 因为 TCP 建立连接是三次握手，所以第一次握手完成后，会添加到服务端的连接队列中。
                .option(ChannelOption.SO_BACKLOG, 1024)
                //TCP Keepalive 机制，实现 TCP 层级的心跳保活功能
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //允许较小的数据包的发送，降低延迟
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(nettyServerHandlerInitializer);
        ChannelFuture future = bootstrap.bind().sync();
        if (future.isSuccess()) {
            channel = future.channel();
            log.info("[start][Netty Server 启动在 {} 端口]", port);
        }
    }

    /**
     * 关闭
     */
    @PreDestroy
    public void shutdown() {
        // <3.1>  调用 Channel 的 #close() 方法，关闭 Netty Server，这样客户端就不再能连接了
        if (channel != null) {
            channel.close();
        }
        // <3.2> 优雅关闭两个 EventLoopGroup 对象
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
