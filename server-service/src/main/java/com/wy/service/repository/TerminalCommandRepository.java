package com.wy.service.repository;

import com.wy.common.enums.TerminalCommandStatus;
import com.wy.service.entity.TerminalCommandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * 命令执行状态描述
 * <p>
 * [ New ] => [ Wait ] => [ Processing ] => [ Success | MessageIsError | NotSupport | Failed ] # 正常执行流程
 * <p>
 * [ New ] => [ Wait ] => [ Invalid ] # 命令格式错误, 或解析命令数据异常
 * <p>
 * [ New ] => [ Offline ] => [ Wait ... -> Processing ...] # 终端离线, 等待终端上线再次处理
 *
 * @author
 */
public interface TerminalCommandRepository extends JpaRepository<TerminalCommandEntity, Long>, JpaSpecificationExecutor<TerminalCommandEntity> {

    List<TerminalCommandEntity> findBySnAndCreateDateAfterOrderByCreateDateDesc(short sn, Date dateTime);

    /**
     * 命令已经成功下发, 更新执行结果 Processing -> Success | MessageIsError | NotSupport | Failed
     *
     * @param onlineNo
     * @param platformSn
     * @param status
     * @param now
     * @param oldStatus
     */
    @Query(
            "update TerminalCommandEntity set status = ?3 , updateDate = ?4 where onlineNo = ?1 and sn = ?2 and status = ?5"
    )
    @Modifying
    void updateState(String onlineNo, short platformSn, TerminalCommandStatus status, Date now, TerminalCommandStatus oldStatus);

    /**
     * 命令成功读取并解析下发, 更新执行结果 Wait -> Processing
     *
     * @param onlineNo   上线号
     * @param platformSn 更新的平台流水号
     * @param cmdType    命令ID
     * @param status     更新的状态
     * @param updateDate 更新时间
     * @param oldStatus  待处理的命令状态
     */
    @Query(
            "update TerminalCommandEntity set status = ?4 ,sn = ?2, updateDate = ?5 where onlineNo = ?1 and cmdType = ?3 and status = ?6"
    )
    @Modifying
    void updateCommandStatus(String onlineNo, short platformSn, int cmdType, TerminalCommandStatus status, Date updateDate, TerminalCommandStatus oldStatus);


    /**
     * 命令成功读取, 准备解析命令 , 更新执行状态 New -> Wait | Wait -> Invalid
     *
     * @param commandId  命令表ID
     * @param status     更新的状态
     * @param updateDate 更新时间
     */
    @Query(
            "update TerminalCommandEntity set status = ?2, updateDate = ?3 where id = ?1"
    )
    @Modifying
    void updateStatus(Long commandId, TerminalCommandStatus status, Date updateDate);

    /**
     * 获取最新的终端命令 -> New | -> offline
     *
     * @param onlineNoList
     * @param status
     * @return
     */
    List<TerminalCommandEntity> findByOnlineNoInAndStatusIn(List<String> onlineNoList, List<TerminalCommandStatus> status);


    /**
     * 查询最后的多媒体命令
     *
     * @param takePhoto
     * @param audioRecorder
     * @param mediaUploadSingle
     * @param mediaUpload
     * @param onlineNo
     * @return
     */
    @Query(nativeQuery = true, value =
            "select * from terminal_command where (cmd_type = ?1 or cmd_type= ?2 or cmd_type= ?3 or cmd_type = ?4 ) and online_no = ?5 order by create_date desc limit 1"
    )
    TerminalCommandEntity getLatestMediaCommand(int takePhoto, int audioRecorder, int mediaUploadSingle, int mediaUpload, String onlineNo);

}
