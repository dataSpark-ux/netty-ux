package com.wy.app;

import com.wy.core.config.CollectProperty;
import com.wy.collect.collect.t808.T808TcpServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wy
 * @Description
 * @createTime 2020/02/01
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "collect", value = "enable", havingValue = "true")
public class NettyServer implements InitializingBean, DisposableBean {

    EventLoopGroup bossGroup;

    EventLoopGroup workerGroup;

    ChannelFuture future;

    ServerBootstrap bootstrap;
    @Resource
    CollectProperty collectProperty;
    @Resource
    T808TcpServerChannelInitializer t808TcpServerChannelInitializer;

    /**
     * 开启Netty服务
     */
    public boolean start() {
        // (1)
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        //
        try {
            // (2)
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    // (3)
                    .channel(NioServerSocketChannel.class)
                    // (4)
                    .childHandler(t808TcpServerChannelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, false);

            // 绑定端口，开始接收进来的连接
            log.info("开启Netty服务器，监听端口 : " + collectProperty.getListenPort());
            future = bootstrap
                    .bind(collectProperty.getListenPort())
                    .sync();
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    public void destroy() throws Exception {
        log.error("netty server stop");
        this.closeChannel();
        this.closeEventLoop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }


    private void closeChannel() {
        try {
            future.channel()
                    .close();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {

        }
    }

    private void closeEventLoop() {
        try {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
