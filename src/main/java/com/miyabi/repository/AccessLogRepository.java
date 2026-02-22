package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.AccessLog;

public interface AccessLogRepository extends JpaRepository<AccessLog, Integer> {
}