package com.wy.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.util.Date;

/**
 * 终端Socket连接信息
 * @author wangyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
public class ConnectionEntity {

    /**
     * 上线号
     */
    @Id
    @Column(columnDefinition = "varchar(16) comment'上线号'")
    private String onlineNo;

    /**
     * 车牌号
     */
    @Column(columnDefinition = "varchar(16) comment'车牌号'")
    private String plateNo;

    /**
     * 创建时间
     */
    @CreatedDate
    private Date createDate;

    /**
     * 在线时间 - 有终端消息上来时刷新此时间
     */
    @LastModifiedDate
    private Date onlineDate;

    /**
     * 最后一次上报时间
     */
    private Date updateDate;

    /**
     * 断开次数
     */
    @Column(columnDefinition = "int(9) default 0 comment '断开次数'")
    private int disconnectTimes;

    /**
     * 连接次数次数
     */
    @Column(columnDefinition = "int(9) default 0 comment '连接次数'")
    private int connectTimes;

    /**
     * 是否连接成功 - 终端有消息时判断设置TRUE , TCP连接移除时 FALSE
     */
    @Column(columnDefinition = "int(1) default 0 comment '是否连接成功'")
    private boolean connected;

    /**
     * 连接的是哪个负载
     */
    @Column(columnDefinition = "varchar(16) default '' comment '连接的是哪个负载'")
    private String system;

    /**
     *
     */
    @Column(columnDefinition = "varchar(32) default '' comment '客户端IP'")
    private String clientIp;
}
