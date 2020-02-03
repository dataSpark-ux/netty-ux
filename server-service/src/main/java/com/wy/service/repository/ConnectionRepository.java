package com.wy.service.repository;

import com.wy.service.entity.ConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author wy
 * @Description
 * @createTime 2020/02/03
 */
public interface ConnectionRepository extends JpaRepository<ConnectionEntity, String>, JpaSpecificationExecutor<ConnectionEntity> {

    /**
     * 1
     * @param system
     * @param isConnected
     * @return
     */
    List<ConnectionEntity> findBySystemAndConnected(String system, boolean isConnected);


    /**
     * 连接断开
     *
     * @param onlineNo
     * @param updateDate 更新时间
     */
    @Query(
            "Update ConnectionEntity set connected = false, disconnectTimes = disconnectTimes + 1 , updateDate = ?2 where onlineNo = ?1 "
    )
    @Modifying
    void disConnected(String onlineNo, Date updateDate);

    /**
     * 更新时肯定是在线, 在线时间和更新时间同步
     *
     * @param onlineNo
     * @param date
     */
    @Query(
            "Update ConnectionEntity set connected = true, updateDate = ?2 where onlineNo = ?1 "
    )
    @Modifying
    void updateIsOnline(String onlineNo, Date date);

    /**
     * 获取目前该负载上所有连接的上线号
     *
     * @param system
     * @return
     */
    @Query(
            "select onlineNo from ConnectionEntity where system = ?1 and connected = true"
    )
    List<String> findOnlineNoBySystem(String system);

    /**
     * 批量下线
     *
     * @param idList
     * @param
     */
    @Query(
            "Update ConnectionEntity set connected = false where onlineNo in ?1 "
    )
    @Modifying
    void updateOffline(List<String> idList);
}
