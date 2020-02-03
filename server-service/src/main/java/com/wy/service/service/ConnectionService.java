package com.wy.service.service;

import com.wy.core.aop.lock.Lock;
import com.wy.core.config.ApplicationProperty;
import com.wy.service.entity.ConnectionEntity;
import com.wy.service.repository.ConnectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 连接管理服务 => 终端上线、终端下线、终端在线状态更新、连接信息管理
 * <p>
 *
 * @author Created by Kun Tang on 2019/2/25.
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private GpsRealDataService gpsRealDataService;

    @Autowired
    private ApplicationProperty applicationProperty;

    /**
     * 获取目前该负载上在线的连接
     *
     * @return
     */
    public List<ConnectionEntity> findOnline() {
        return connectionRepository.findBySystemAndConnected(applicationProperty.getSystem(), true);
    }

    /**
     * 获取目前该负载上所有连接的上线号
     *
     * @return OnlineNo
     */
    public List<String> findAllOnlineNo() {
        return connectionRepository.findOnlineNoBySystem(applicationProperty.getSystem());
    }

    /**
     * 终端上线, 更新时间和负载标识
     *
     * @param onlineNo
     * @return
     */
    @Lock(prefix = "connection_", key = "#onlineNo")
    public ConnectionEntity online(String onlineNo, String clientIp) {
        Optional<ConnectionEntity> connection = connectionRepository.findById(onlineNo);
        ConnectionEntity one = connection.get();
        if (one == null) {
            one = ConnectionEntity
                    .builder()
                    .onlineNo(onlineNo)
                    .connected(true)
                    .disconnectTimes(0)
                    .build();
        }
        one.setClientIp(clientIp);
        one.setConnectTimes(one.getConnectTimes() + 1);
        one.setSystem(applicationProperty.getSystem());
        one.setConnected(true);
        one.setOnlineDate(new Date());
        connectionRepository.save(one);

        // 终端上线
        gpsRealDataService.updateIsOnline(onlineNo);
        return one;
    }

    /**
     * 终端下线
     *
     * @param onlineNo
     */
    @Lock(prefix = "connection_", key = "#onlineNo")
    public void offline(String onlineNo) {
        connectionRepository.disConnected(onlineNo, new Date());
        gpsRealDataService.updateIsOffline(onlineNo);
    }

    /**
     * 上报数据的时候 => 更新在线时间和在线状态[在线]
     *
     * @param onlineNo
     */
    public void update(String onlineNo) {
        connectionRepository.updateIsOnline(onlineNo, new Date());
        gpsRealDataService.updateIsOnline(onlineNo);
    }

    /**
     *
     */
    public List<String> offlineBySystem() {
        List<String> idList = findAllOnlineNo();
        if (idList.isEmpty()) {
            return idList;
        }
        idList.forEach(gpsRealDataService::updateIsOffline);
        connectionRepository.updateOffline(idList);
        return idList;
    }
}
