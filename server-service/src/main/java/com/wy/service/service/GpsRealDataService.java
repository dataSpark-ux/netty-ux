package com.wy.service.service;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import com.wy.core.aop.watch.Watch;
import com.wy.core.cache.MemoryCache;
import com.wy.core.protocol.T808Message;
import com.wy.service.entity.GPSRealData;
import com.wy.service.entity.VehicleDataEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 实时数据服务, 缓存最新的终端状态数据
 *
 * @Author 2019-01-15
 */
@Slf4j
@Service
public class GpsRealDataService implements ApplicationContextAware, RemovalListener<String, GPSRealData> {

    @Autowired
    private VehicleDataService vehicleDataService;

    @Autowired
    private FilterService<T808Message> filterService;

    private MemoryCache<String, GPSRealData> memoryCache;

    /**
     * @param onlineNo
     * @return
     */
    public GPSRealData get(String onlineNo) {
        GPSRealData realData = memoryCache.get(toKey(onlineNo));
        if (realData != null) {
            return realData;
        }

        VehicleDataEntity vd = vehicleDataService.findByOnlineNo(onlineNo);
        GPSRealData gpsRealData = GPSRealData
                .builder()
                .onlineNo(onlineNo)
                .vehicleId(vd.getId())
                .plateNo(vd.getPlateNo())
                .userId(vd.getUserId())
                .groupId(vd.getGroupId())
                .parkingTime(new Date())
                .updateDate(new Date())
                .sendTime(new Date())
                .onlineDate(new Date())
                .online(true)
                .deviceTypeId(vd.getDeviceTypeId())
                .build();
        memoryCache.set(toKey(onlineNo), gpsRealData);
        return gpsRealData;
    }

    /**
     * @param rd
     */
    @Watch(value = "存储GPSRealData", limit = 100, limitUnit = TimeUnit.MILLISECONDS)
    public GPSRealData edit(GPSRealData rd) {
        // 刷新更新时间
        rd.setUpdateDate(new Date());
        memoryCache.set(toKey(rd.getOnlineNo()), rd);
        return rd;
    }

    /**
     * 更新最后在线时间和在线状态[离线]
     *
     * @param onlineNo
     */
    public void updateIsOffline(String onlineNo) {
        try {
            GPSRealData gpsRealData = get(onlineNo);
            gpsRealData.setOnline(false);
            // 实时数据刷新确定最后在线时间
            // gpsRealData. setOnlineDate(new Date());
            edit(gpsRealData);
        } catch (Exception e) {
            // 终端下线查询不存在车辆异常, 对此终端的限制移除
            filterService.clear(onlineNo);
        }
    }

    /**
     * 更新在线时间和在线状态[在线]
     *
     * @param onlineNo
     */
    public void updateIsOnline(String onlineNo) {
        GPSRealData gpsRealData = get(onlineNo);
        gpsRealData.setOnlineDate(new Date());
        gpsRealData.setOnline(true);
        edit(gpsRealData);
    }

    private String toKey(String online) {
        return "grdrkp_" + online;
    }


    @Override
    public void onRemoval(RemovalNotification<String, GPSRealData> notification) {
        switch (notification.getCause()) {
            case EXPIRED:
                if (log.isDebugEnabled()) {
                    log.debug(String.format("车辆[%s] => 超过1分钟没有上报GPS, 过期", notification.getKey()));
                }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        memoryCache = new MemoryCache<>(
                1,
                TimeUnit.MINUTES,
                this,
                (RedisTemplate<String, GPSRealData>) applicationContext.getBean("redisTemplate"));
    }
}
