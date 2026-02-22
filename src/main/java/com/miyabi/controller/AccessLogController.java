package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.AccessLog;
import com.miyabi.service.AccessLogService;

@RestController
@RequestMapping("/api/access-logs")
public class AccessLogController {

    private final AccessLogService accessLogService;

    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @GetMapping
    public List<AccessLog> getAllLogs() {
        return accessLogService.findAll();
    }

    @PostMapping
    public AccessLog createLog(@RequestBody AccessLog accessLog) {
        return accessLogService.save(accessLog);
    }
}