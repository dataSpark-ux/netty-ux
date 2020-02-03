package com.wy.service.entity;

import com.wy.common.enums.TerminalCommandStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 终端命令 历史表
 * @author wangyi
 */
@Entity
@Data
@AllArgsConstructor
@Table(name = "terminal_up_command")
@NoArgsConstructor
@Builder
public class TerminalUpCommandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;
    /**
     * 命令的发送者
     */
    @Column(name = "user_id")
    private long userId;
    /**
     * 类别 命令的大类别
     */
    @Column(name = "cmd_type")
    private int cmdType;
    /**
     * // 终端ID号
     * // 命令数据中的命令字或标志位
     */
    @Column(name = "cmd")
    private String cmd;
    /**
     * 数据，多个参数的时候，使用;隔开
     */
    @Column(name = "cmd_data")
    private String cmdData;
    /**
     * 命令下发的流水号
     */
    @Column(name = "sn")
    private short sn;
    /**
     * 上线号
     */
    @Column(name = "online_no")
    private String onlineNo;
    /**
     * 车牌号
     */
    @Column(name = "plate_no")
    private String plateNo;
    /**
     * 车辆Id
     */
    @Column(name = "vehicle_id")
    private String vehicleId;
    /**
     * 命令状态
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TerminalCommandStatus status;
    /**
     * 创建日期
     */
    @Column(name = "create_date")
    private Date createDate = new Date();
    /**
     * 命令执行时间
     */
    @Column(name = "update_date")
    private Date updateDate;
    /**
     * 假删除标记
     */
    @Column(name = "deleted", columnDefinition = "bit DEFAULT 0 ")
    private boolean deleted;
    /**
     * 所属者 [终端主动上报信息, 平台主动下发命令]
     */
    @Column(name = "owner")
    @Enumerated(EnumType.STRING)
    private TerminalCommandStatus owner;
    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;
}