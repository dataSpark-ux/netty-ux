package com.wy.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 车辆基本静态信息
 *
 * @author wangyi
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vehicle")
public class VehicleDataEntity implements Serializable {

    /**
     * 车辆编号
     */
    @Id
    @Column
    private String id;
    /**
     * 上线号
     */
    @Column(name = "online_no")
    private String onlineNo;
    /**
     * 主账号ID
     */
    @Column(name = "user_id")
    private String userId;
    /**
     * 车牌号
     */
    @Column(name = "licenseno")
    private String plateNo;
    /**
     * 车牌颜色
     */
    @Column(name = "license_color")
    private Integer plateColor;
    /**
     * GPS手机卡号
     */
    @Column(name = "simcard_no")
    private String simcardNo;
    /**
     *
     */
    @Column(name = "device_no")
    private String deviceNo;
    /**
     * 服务类型
     */
    @Column(name = "service_type")
    private String serviceType;
    /**
     * 分组编号
     */
    @Column(name = "group_id")
    private String groupId;
    /**
     * 设别类型编号
     */
    @Column(name = "device_typeid")
    private String deviceTypeId;
    /**
     * 假删除标记
     */
    @Column(name = "del_flag")
    private boolean deleted;

    public static VehicleDataEntity build(GPSRealData gpsRealData) {
        return VehicleDataEntity
                .builder()
                .id(gpsRealData.getVehicleId())
                .onlineNo(gpsRealData.getOnlineNo())
                .userId(gpsRealData.getUserId())
                .plateNo(gpsRealData.getPlateNo())
                .groupId(gpsRealData.getGroupId())
                .deviceTypeId(gpsRealData.getDeviceTypeId())
                .build();
    }

}