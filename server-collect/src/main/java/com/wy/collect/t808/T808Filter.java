package com.wy.collect.t808;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.sinoxx.sserver.core.cache.MemoryCache;
import com.sinoxx.sserver.core.protocol.jt808.T808Message;
import com.sinoxx.sserver.core.util.ToolBuff;
import com.sinoxx.sserver.service.service.filter.FilterService;
import com.wy.service.service.FilterService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 消息拦截器 - 对特定消息进行转发和拦截
 * fasle 不拦截 、true 拦截
 *
 * @Author 2018-12-18
 */
@Log4j
@Service
public class T808Filter implements FilterService<T808Message>, ApplicationContextAware, RemovalListener<String, Date> {

    /**
     * 过滤黑名单列表 <上线号, 过滤的创建更新时间点>
     */
    private MemoryCache<String, Date> filterMemoryCache;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void filter(T808Message msg) {
        filterMemoryCache.set(toKey(msg.getSession()), new Date());
    }

    /**
     * 将上线号置入黑名单
     *
     * @param key 上线号
     */
    @Override
    public void filter(String key) {
        filterMemoryCache.set(toKey(key), new Date());

        // 判断过滤时, 删除缓存信息
        redisTemplate.delete(toGpsRealDataKey(key));
    }

    @Override
    public void clear(String key) {
        filterMemoryCache.delete(toKey(key));
    }

    @Override
    public Date get(String key) {
        return filterMemoryCache.get(toKey(key));
    }

    /**
     * 808消息字节序号
     */
    public boolean isFilterMsg(byte[] messageBytes) {
        if (messageBytes.length < 12) {
            return false;
        }
        // 7E 0200 0000 00 00 00 00 00 00
        byte[] onlineNoBytes = new byte[6];
        for (int i = 5; i < 11; i++) {
            onlineNoBytes[i - 5] = messageBytes[i];
        }
        return isFilterMsgOfOnlineNo(onlineNoBytes);
    }

    /**
     * 六位字节的上线号
     */
    private boolean isFilterMsgOfOnlineNo(byte[] onlineNoBytes) {
        return isFilterMsg(bytesToOnline(onlineNoBytes));
    }

    /**
     * 判断是否过滤
     *
     * @param onlineNo
     * @return
     */
    private boolean isFilterMsg(String onlineNo) {
        Date d = filterMemoryCache.get(toKey(onlineNo));
        if (d != null) {
            // 过滤
            return true;
        }
        return false;
    }

    /**
     * 上线号转化为刘为字节序号
     *
     * @param onlineNo
     * @return
     */
    private static byte[] onlineNoToBytes(String onlineNo) {
        return ToolBuff.strToBCDBytes(onlineNo);
    }

    /**
     * 六位字节转化为上线号
     *
     * @param onlineNoBytes
     * @return
     */
    private String bytesToOnline(byte[] onlineNoBytes) {
        return ToolBuff.bcdBytesToStr(onlineNoBytes);
    }

    private String toKey(String onlineNo) {
        return "filter_" + onlineNo;
    }

    @Override
    public void onRemoval(RemovalNotification<String, Date> notification) {
        switch (notification.getCause()) {
            case EXPIRED:
                if (log.isDebugEnabled()) {
                    // 移除过滤限制
                    filterMemoryCache.delete(notification.getKey());
                    log.debug(String.format("过滤限制时间 => 超过10分钟, 过期", notification.getKey()));
                }
                break;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        filterMemoryCache = new MemoryCache<>(
                10,
                TimeUnit.MINUTES,
                this,
                (RedisTemplate<String, Date>) applicationContext.getBean("redisTemplate"));
    }

    private String toGpsRealDataKey(String online) {
        // 实时数据KEY
        return "grdrkp_" + online;
    }

}