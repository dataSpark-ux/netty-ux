package com.wy.service.service;

import com.wy.common.enums.ResultCodeEnum;
import com.wy.core.exception.BizException;
import com.wy.core.protocol.T808Message;
import com.wy.service.entity.VehicleDataEntity;
import com.wy.service.repository.VehicleDataRepository;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kun Tang on 2019/2/18.
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
@CacheConfig(cacheNames = "vehicle")
public class VehicleDataService {

    @Autowired
    private VehicleDataRepository vehicleDataRepository;

    @Autowired
    private FilterService<T808Message> filterService;

    public VehicleDataEntity findByOnlineNo(String onlineNo) {
        VehicleDataEntity vehicleData = null;
        try {
            vehicleData = vehicleDataRepository.findByOnlineNo(onlineNo);
            if (vehicleData == null) {
                // 查询不到车辆
                filterService.filter(onlineNo);
                throw new BizException(ResultCodeEnum.OPERATION_FAILED.getCode(),onlineNo);
            }
        } catch (Exception e) {

        }
        return vehicleData;
    }

}
