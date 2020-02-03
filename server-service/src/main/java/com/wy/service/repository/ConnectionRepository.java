package com.wy.service.repository;

import com.wy.service.entity.ConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author wy
 * @Description
 * @createTime 2020/02/03
 */
public interface ConnectionRepository extends JpaRepository<ConnectionEntity, String>, JpaSpecificationExecutor<ConnectionEntity> {
}
