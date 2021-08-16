package com.monetware.ringsurvey.business.controller;

import com.monetware.ringsurvey.business.service.ScheduleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Api("定时管理")
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 每天凌晨0点执行
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void everyDayExecute() {
        scheduleService.updateProjectRelease();
    }


}
