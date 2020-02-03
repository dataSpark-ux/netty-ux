package com.wy.service.repository;

import com.wy.service.entity.TerminalUpCommandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author wangyi
 */
public interface TerminalUpCommandRepository extends JpaRepository<TerminalUpCommandEntity, Long>, JpaSpecificationExecutor<TerminalUpCommandEntity> {


}
