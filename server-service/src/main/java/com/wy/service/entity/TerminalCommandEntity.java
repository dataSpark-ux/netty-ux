package com.wy.service.entity;

import cn.hutool.core.util.StrUtil;
import com.wy.common.enums.TerminalCommandStatus;
import com.wy.core.exception.BizException;
import com.wy.core.queue.IMsg;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 终端命令
 *
 */
@Entity
@Data
@Table(name = "Terminal_Command")
@org.hibernate.annotations.Proxy(lazy = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminalCommandEntity implements Serializable, IMsg<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;
    /**
     * 命令的发送者
     */
    @Column(name = "user_id")
    private String userId;
    /**
     * 发送的用户名
     */
    @Column(name = "user_name")
    private String userName;
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

    @Override
    public String getHash() {
        return getOnlineNo();
    }

    @Override
    public Integer getMessageType() {
        return getCmdType();
    }

    /**
     * 只获取第一个值 cmd [1,2 => 1]
     *
     * @return
     */
    public byte getByteCmd() {
        String[] split = StrUtil.split(getCmd(), ",");
        if (split.length == 2) {
            return Byte.parseByte(split[0]);
        } else {
            throw new BizException();
        }
    }

    public String getCmd() {
        if (StrUtil.isBlank(cmd)) {
            return "";
        }
        return cmd;
    }

    public String getCmdData() {
        if (StrUtil.isBlank(cmdData)) {
            return "";
        }
        return cmdData;
    }

    /**
     * cmd数据 [id,参数值;id,参数值;...]
     *
     * @param splitChar
     * @param function
     * @param <T>
     * @return
     */
    public <T> List<T> getCMDSplit(String splitChar, Function<String, T> function) {
        return Stream
                .of(StringUtils.split(getCmd(), splitChar))
                .map(function)
                .collect(Collectors.toList());
    }

    public <T> List<T> getCmdDataSplit(String splitChar, Function<String, T> function) {
        return Stream
                .of(StringUtils.split(getCmdData(), splitChar))
                .map(function)
                .collect(Collectors.toList());
    }

    /**
     * cmdData 数据 [参数值;参数值;参数值;...]
     *
     * @param s
     * @return
     */
    public String[] getCmdDataSplit(String s) {
        return StringUtils.split(getCmdData(), s);
    }

}