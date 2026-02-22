package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.miyabi.models.AccessLog;
import com.miyabi.repository.AccessLogRepository;

@Service
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;

    public AccessLogService(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    public List<AccessLog> findAll() {
        return accessLogRepository.findAll();
    }

    public AccessLog save(AccessLog accessLog) {
        return accessLogRepository.save(accessLog);
    }
}