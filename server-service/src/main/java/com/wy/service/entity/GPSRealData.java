package com.wy.service.entity;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * GPS实时数据 - 每个车一条实时记录，用于保存当前最新的gps定位数据
 *
 * @author
 */
@AllArgsConstructor
@Builder
@Data
public class GPSRealData implements Serializable {

    /**
     * 车辆编号
     */
    private String vehicleId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 分组编号
     */
    private String groupId;
    /**
     * 车牌号
     */
    private String plateNo;
    /**
     * 上线号
     */
    private String onlineNo;
    /**
     * 设备终端状态
     */
    private String status;
    /**
     * 电路状态,也就是报警状态
     */
    private String alarmState;
    /**
     * 刹车转向等状态, 附加信息0x25扩展车辆信号状态位
     */
    private int signalState;
    /**
     * 行驶里程总量
     */
    private double mileage;
    /**
     * 油量, 附加信息0x02对应车辆油量表读读数
     */
    private double gas;
    /**
     * gps速度
     */
    private double velocity;
    /**
     * 行驶记录仪速度，附加信息0x03行驶记录仪获取的速度
     */
    private double recordVelocity;
    /**
     * 经度
     */
    private double longitude;
    /**
     * 纬度
     */
    private double latitude;
    /**
     * 海拔高度
     */
    private double altitude;
    /**
     * 方向
     */
    private int direction;
    /**
     * 地理位置的文字描述,如省,市，县，路的详细描述
     */
    private String location;
    /**
     * GPS设备在线状态, false代表不在线
     */
    private boolean online;
    /**
     * 在线时间
     */
    private Date onlineDate;
    /**
     * 是否定位, GPS信号是否有定位, GPS的定位状态，false代表没有定位,被屏蔽或找不到卫星
     */
    private boolean valid;
    /**
     * 位置数据发送时间
     */
    private Date sendTime;
    /**
     * 最近一次停车时间
     */
    private Date parkingTime;
    /**
     * 入库更新时间
     */
    private Date updateDate;
    /**
     * 昨天里程(累计到昨日的总里程)，当天行驶里程 = mileage - yesterdayMileage
     */
    private double lastDayMileage;
    /**
     * GNSS 定位卫星数
     */
    private Integer sat;

    /**
     * 终端发送的定位包的终端流水号 - 不入库数据表
     */
    private int responseSn;

    /**
     * 设备类型编号
     * 138 迪纳
     * 145 车葫芦
     */
    private String deviceTypeId;

    /********** ********** 报警分析数据 ********** **********/

//    /**
//     * 迪纳 , 场景事件报警
//     */
//    @Column(name = "alarm_data")
//    private String eventAlarmData;

    /**
     * 终端围栏报警的区域ID
     */
    private long areaId;
    /**
     * 围栏报警类型 0：进； 1：出
     */
    private int areaAlarm;
    /**
     * 围栏报警的区域类型
     */
    private int areaType;

    /**
     * 超速时，所在的区域id
     */
    private int overSpeedAreaId;
    /**
     * 超速时，所在的区域,区域类型
     */
    private int overSpeedAreaType;

    /**
     * 路段行驶时间过长报警时，所在的路段id
     */
    private int routeSegmentId;
    /**
     * 0：行驶时间不足，1：过长
     */
    private int routeAlarmType;
    /**
     * 行驶时间
     */
    private int runTimeOnRoute;

    /**
     * 疲劳驾驶报警时间
     */
    private Date tiredAlarmTime;


    /**
     * 视频在线状态
     */
    private String dvrStatus;
    /**
     * 平台路线偏离报警
     */
    private String offsetRouteAlarm;
    /**
     * 平台分段限速报警
     */
    private String overSpeedAlarm;
    /**
     * 平台地图区域报警
     */
    private String mapAreaAlarm;
    /**
     * 平台未按规定时间到达关键点报警
     */
    private String arriveKeyPlaceAlarm;
    /**
     * 平台未按规定时间离开关键点报警
     */
    private String leaveKeyPlaceAlarm;

    /**
     * 无线通信网络信号强度
     */
    private byte signalStrength;

    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;

    public GPSRealData() {
        alarmState = "00000000000000000000000000000000";
        status = "00000000000000000000000000000000";
        //setSendTime(new Date());
        this.updateDate = new Date();
    }

    @JsonIgnore
    public Integer getIntGroupId() {
        if (StrUtil.isBlank(groupId)) {
            return 0;
        }
        return Integer.parseInt(groupId);
    }


    @Override
    public GPSRealData clone() {
        return GPSRealData
                .builder()
                .vehicleId(this.vehicleId)
                .userId(this.userId)
                .groupId(this.groupId)
                .plateNo(this.plateNo)
                .onlineNo(this.onlineNo)
//                .status(this.status)
//                .alarmState(this.alarmState)
//                .signalState(this.signalState)
//                .mileage(this.mileage)
//                .gas(this.gas)
                .velocity(this.velocity)
                .recordVelocity(this.recordVelocity)
                .longitude(this.longitude)
                .latitude(this.latitude)
                .altitude(this.altitude)
                .direction(this.direction)
//                .location(this.location)
                .online(this.online)
                .onlineDate(this.onlineDate)
                .valid(this.valid)
                .sendTime(this.sendTime)
//                .parkingTime(this.parkingTime)
                .updateDate(this.updateDate)
//                .lastDayMileage(this.lastDayMileage)
//                .sat(this.sat)
//                .responseSn(this.responseSn)
                .deviceTypeId(this.deviceTypeId)
                .build();
    }

}