package com.monetware.ringsurvey.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @Author: lu
 * @Date: 2020/04/02 16:08
 * @Description: 仪表盘
 **/
@Data
public class SampleDashboardDTO {

    /**
     * 使用率
     */
    private Float useRate;

    /**
     * 有效率
     */
    private Float validRate;

    /**
     * 成功率
     */
    private Float successRate;

    /**
     * 有效成功率
     */
    private Float validSuccessRate;

    /**
     * 拒绝率
     */
    private Float refuseRate;

    /**
     * 有效拒绝率
     */
    private Float validRefuseRate;

    /**
     * 审核率
     */
    private Float auditRate;

    /**
     * 审核成功率
     */
    private Float auditSuccessCount;


}
