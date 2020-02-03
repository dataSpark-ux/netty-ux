package com.wy.collect.collect;

import cn.hutool.core.util.StrUtil;
import com.wy.core.protocol.IPackage;
import com.wy.core.trace.Trace;
import com.wy.service.service.ConnectionService;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接管理器 - 管理一种消息类型, 对应终端的所有连接
 * <p>
 * Created by Kun Tang on 2019/2/12.
 */
@Slf4j
public abstract class ConnectionManager<T extends IPackage> implements InitializingBean, DisposableBean {

    private static final AttributeKey<String> KEY = AttributeKey.valueOf("onlineNo");

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private Trace trace;

    /**
     * 缓存所有连接信息 - <标识符, 终端连接对象>
     */
    private Map<String, Channel> channelCache = new ConcurrentHashMap<>();

    /**
     * 返回管理的对应的消息类型
     *
     * @return
     */
    public abstract Class<? extends IPackage> support();

    /**
     * 下发消息
     *
     * @param iPackage
     * @return
     */
    public boolean send(T iPackage) {
        boolean ok = sendInternal(iPackage);
        trace.down(iPackage, iPackage.getBytes(), ok);
        return ok;
    }


    /**
     * 更新连接channel状态, 并更新在线时间和在线状态
     *
     * @param onlineNo
     * @param channel
     */
    public void connection(String onlineNo, Channel channel) {
        // 已关联
        if (channelCache.containsKey(onlineNo)) {
            connectionService.update(onlineNo);
            return;
        }
        // 未关联, 关联一下, 更新连接状态
        channel.attr(KEY)
               .set(onlineNo);
        channelCache.put(onlineNo, channel);
        connectionService.online(onlineNo, channel.remoteAddress()
                                                  .toString());
        connection(onlineNo);
    }

    /**
     * @param channel
     * @return
     */
    public String getOnline(Channel channel) {
        return channel.attr(KEY)
                      .get();
    }

    /**
     * 中断连接, 更新状态, 删除关联
     *
     * @param channel
     */
    public void disconnection(Channel channel) {
        String onlineNo = getOnline(channel);
        if (StrUtil.isNotBlank(onlineNo)) {
            channelCache.remove(onlineNo);
            connectionService.offline(onlineNo);
        }
    }

    /**
     * 设置所有连接在该主机的终端下线
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        List<String> idList = connectionService.offlineBySystem();
        log.info(String.format("服务器关闭, 批量让连接到负载的终端下线, 共有[ %d ]个终端", idList.size()));
        channelCache.forEach((k, v) -> v.close());
    }

    /**
     * 获取对应终端的连接
     *
     * @param key
     * @return
     */
    public Channel getChannel(String key) {
        return channelCache.get(key);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    /**
     * 内部下发消息
     *
     * @param iPackage
     * @return
     */
    protected abstract boolean sendInternal(T iPackage);

    /**
     *
     */
    protected abstract void connection(String onlineNo);

    /**
     * @param onlineNo
     */
    protected abstract void update(String onlineNo);
}
