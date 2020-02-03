package com.wy.service.repository;

import com.wy.service.entity.VehicleDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author wy
 */
public interface VehicleDataRepository extends JpaRepository<VehicleDataEntity, String>, JpaSpecificationExecutor<VehicleDataEntity> {

    /**
     * 通过上线号查找唯一车辆
     *
     * @param onlineNo
     * @return
     */
    VehicleDataEntity findByOnlineNo(String onlineNo);
}
