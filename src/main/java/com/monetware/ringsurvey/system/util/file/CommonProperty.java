package com.monetware.ringsurvey.system.util.file;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommonProperty {

    public static Map<String, String> teamHeadMap = new HashMap<>();

    public static Map<String, String> sampleHeadMap = new HashMap<>();

    public static Map<String, String> sampleDBMap = new HashMap<>();// 样本DB字段对应map

    public static Map<String, String> responseHeadMap = new HashMap<>();

    public static Map<String, String> sampleProperty2Field = new HashMap<>();

    public static Map<String, String> redPacketHeadMap = new HashMap<>();

    static {
        // 定义团队导出所有标题
        teamHeadMap.put("id", "团队成员ID");
        teamHeadMap.put("name", "姓名");
        teamHeadMap.put("userName", "用户名");
        teamHeadMap.put("email", "邮箱");
        teamHeadMap.put("telephone", "手机");
        teamHeadMap.put("sampleNum", "样本数量");
        teamHeadMap.put("role", "角色");
        teamHeadMap.put("groupName", "分组");
        teamHeadMap.put("joinDate", "加入日期");
        teamHeadMap.put("validDate", "授权日期");

        // 定义样本导出所有标题
        sampleHeadMap.put("sampleGuid", "样本ID");
        sampleHeadMap.put("name", "样本名称");
        sampleHeadMap.put("code", "样本编码");
        sampleHeadMap.put("gender", "性别");
        sampleHeadMap.put("organization", "单位");
        sampleHeadMap.put("birth", "出生日期");
        sampleHeadMap.put("age", "年龄");
        sampleHeadMap.put("mobile", "电话");
        sampleHeadMap.put("weixin", "微信");
        sampleHeadMap.put("phone", "手机");
        sampleHeadMap.put("qq", "qq");
        sampleHeadMap.put("email", "邮箱");
        sampleHeadMap.put("weibo", "微博");
        sampleHeadMap.put("province", "省");
        sampleHeadMap.put("city", "市");
        sampleHeadMap.put("district", "区/县");
        sampleHeadMap.put("town", "街道/镇");
        sampleHeadMap.put("address", "详细地址");
        sampleHeadMap.put("marriageStatus", "婚姻状况");
        sampleHeadMap.put("education", "学历");
        sampleHeadMap.put("politicalStatus", "政治面貌");
        sampleHeadMap.put("nationality", "国籍");
        sampleHeadMap.put("profession", "职业");
        sampleHeadMap.put("position", "职务");
        sampleHeadMap.put("placeOfBirth", "籍贯");
        sampleHeadMap.put("religion", "宗教信仰");
        sampleHeadMap.put("language", "语言");
        sampleHeadMap.put("dialects", "方言");
        sampleHeadMap.put("description", "备注");
        sampleHeadMap.put("detail", "详细介绍");
        sampleHeadMap.put("contactTimes", "接触次数");
        sampleHeadMap.put("smsTimes", "短信发送次数");
        sampleHeadMap.put("emailTimes", "邮件发送次数");
        sampleHeadMap.put("lastModifyTime", "修改时间");
        sampleHeadMap.put("managerName", "负责人");
        sampleHeadMap.put("recycleTimes", "回收次数");
        sampleHeadMap.put("custom1", "自定义1");
        sampleHeadMap.put("custom2", "自定义2");
        sampleHeadMap.put("custom3", "自定义3");
        sampleHeadMap.put("custom4", "自定义4");
        sampleHeadMap.put("custom5", "自定义5");

        // 定义答卷导出所有标题
        // 样本
        responseHeadMap.put("sampleGuid", "样本ID");
        responseHeadMap.put("name", "样本名称");
        responseHeadMap.put("code", "样本编码");
        responseHeadMap.put("gender", "性别");
        responseHeadMap.put("organization", "单位");
        responseHeadMap.put("birth", "出生日期");
        responseHeadMap.put("age", "年龄");
        responseHeadMap.put("mobile", "电话");
        responseHeadMap.put("weixin", "微信");
        responseHeadMap.put("phone", "手机");
        responseHeadMap.put("qq", "qq");
        responseHeadMap.put("email", "邮箱");
        responseHeadMap.put("weibo", "微博");
        responseHeadMap.put("province", "省");
        responseHeadMap.put("city", "市");
        responseHeadMap.put("district", "区/县");
        responseHeadMap.put("town", "街道/镇");
        responseHeadMap.put("address", "详细地址");
        responseHeadMap.put("marriageStatus", "婚姻状况");
        responseHeadMap.put("education", "学历");
        responseHeadMap.put("politicalStatus", "政治面貌");
        responseHeadMap.put("nationality", "国籍");
        responseHeadMap.put("profession", "职业");
        responseHeadMap.put("position", "职务");
        responseHeadMap.put("placeOfBirth", "籍贯");
        responseHeadMap.put("religion", "宗教信仰");
        responseHeadMap.put("language", "语言");
        responseHeadMap.put("dialects", "方言");
        responseHeadMap.put("description", "备注");
        responseHeadMap.put("detail", "详细介绍");
        responseHeadMap.put("custom1", "自定义1");
        responseHeadMap.put("custom2", "自定义2");
        responseHeadMap.put("custom3", "自定义3");
        responseHeadMap.put("custom4", "自定义4");
        responseHeadMap.put("custom5", "自定义5");
        responseHeadMap.put("contactTimes", "接触次数");
        responseHeadMap.put("smsTimes", "短信发送次数");
        responseHeadMap.put("emailTimes", "邮件发送次数");
        responseHeadMap.put("lastModifyTime", "修改时间");
        responseHeadMap.put("managerName", "负责人");
        responseHeadMap.put("recycleTimes", "回收次数");
        // 访员
        responseHeadMap.put("userEmail", "访员邮箱");
        responseHeadMap.put("userName", "访员姓名");
        responseHeadMap.put("userPhone", "访员电话");
        // 答卷
        responseHeadMap.put("responseId", "答卷编号");
        responseHeadMap.put("responseStatus", "答卷状态");
        responseHeadMap.put("startTime", "答卷开始时间");
        responseHeadMap.put("endTime", "答卷结束时间");
        responseHeadMap.put("responseLen", "答卷时长");
        responseHeadMap.put("auditTime", "答卷审核时间");
        responseHeadMap.put("auditScore", "答卷审核评分");
        responseHeadMap.put("auditDesc", "答卷审核意见");
        responseHeadMap.put("auditNote", "答卷审核批注");
        responseHeadMap.put("auditResult", "答卷审核结果");

        // 样本DB字段
        sampleDBMap.put("name", "样本名称");
        sampleDBMap.put("sample_guid", "样本ID");
        sampleDBMap.put("code", "样本编码");
        sampleDBMap.put("gender", "性别");
        sampleDBMap.put("age", "年龄");
        sampleDBMap.put("birth", "出生日期");
        sampleDBMap.put("marriage_status", "婚姻状况");
        sampleDBMap.put("education", "学历");
        sampleDBMap.put("organization", "单位");
        sampleDBMap.put("profession", "职业");
        sampleDBMap.put("position", "职务");
        sampleDBMap.put("political_status", "政治面貌");
        sampleDBMap.put("religion", "宗教信仰");
        sampleDBMap.put("nationality", "国籍");
        sampleDBMap.put("language", "语言");
        sampleDBMap.put("place_of_birth", "籍贯");
        sampleDBMap.put("dialects", "方言");
        sampleDBMap.put("description", "备注");
        sampleDBMap.put("detail", "详细介绍");
        sampleDBMap.put("email", "邮箱");
        sampleDBMap.put("mobile", "电话");
        sampleDBMap.put("phone", "手机");
        sampleDBMap.put("weixin", "微信");
        sampleDBMap.put("qq", "qq");
        sampleDBMap.put("weibo", "微博");
        sampleDBMap.put("province", "省");
        sampleDBMap.put("city", "市");
        sampleDBMap.put("district", "区/县");
        sampleDBMap.put("town", "街道/镇");
        sampleDBMap.put("address", "详细地址");
        sampleDBMap.put("custom1", "自定义1");
        sampleDBMap.put("custom2", "自定义2");
        sampleDBMap.put("custom3", "自定义3");
        sampleDBMap.put("custom4", "自定义4");
        sampleDBMap.put("custom5", "自定义5");
        sampleDBMap.put("contact_times", "接触次数");
        sampleDBMap.put("sms_times", "短信发送次数");
        sampleDBMap.put("email_times", "邮件发送次数");
        sampleDBMap.put("recycle_times", "回收次数");
        sampleDBMap.put("managerName", "负责人");
        sampleDBMap.put("last_modify_time", "修改时间");

        // 定义样本属性对应字段
        sampleProperty2Field.put("id", "id");
        sampleProperty2Field.put("sampleGuid", "sample_guid");
        sampleProperty2Field.put("name", "name");
        sampleProperty2Field.put("code", "code");
        sampleProperty2Field.put("gender", "gender");
        sampleProperty2Field.put("organization", "organization");
        sampleProperty2Field.put("birth", "birth");
        sampleProperty2Field.put("age", "age");
        sampleProperty2Field.put("mobile", "mobile");
        sampleProperty2Field.put("weixin", "weixin");
        sampleProperty2Field.put("phone", "phone");
        sampleProperty2Field.put("qq", "qq");
        sampleProperty2Field.put("email", "email");
        sampleProperty2Field.put("weibo", "weibo");
        sampleProperty2Field.put("province", "province");
        sampleProperty2Field.put("city", "city");
        sampleProperty2Field.put("district", "district");
        sampleProperty2Field.put("town", "town");
        sampleProperty2Field.put("address", "address");
        sampleProperty2Field.put("marriageStatus", "marriage_status");
        sampleProperty2Field.put("education", "education");
        sampleProperty2Field.put("politicalStatus", "political_status");
        sampleProperty2Field.put("nationality", "nationality");
        sampleProperty2Field.put("profession", "profession");
        sampleProperty2Field.put("position", "position");
        sampleProperty2Field.put("placeOfBirth", "place_of_birth");
        sampleProperty2Field.put("religion", "religion");
        sampleProperty2Field.put("language", "language");
        sampleProperty2Field.put("dialects", "dialects");
        sampleProperty2Field.put("description", "description");
        sampleProperty2Field.put("detail", "detail");
        sampleProperty2Field.put("custom1", "custom1");
        sampleProperty2Field.put("custom2", "custom2");
        sampleProperty2Field.put("custom3", "custom3");
        sampleProperty2Field.put("custom4", "custom4");
        sampleProperty2Field.put("custom5", "custom5");
        sampleProperty2Field.put("contactTimes", "contact_times");
        sampleProperty2Field.put("smsTimes", "sms_times");
        sampleProperty2Field.put("emailTimes", "email_times");
        sampleProperty2Field.put("recycleTimes", "recycle_times");
        sampleProperty2Field.put("lastModifyTime", "last_modify_time");
        sampleProperty2Field.put("managerName", "managerName");

        // 红包导出标题
        redPacketHeadMap.put("id", "记录ID");
        redPacketHeadMap.put("responseId", "答卷编号");
        redPacketHeadMap.put("responseGuid", "答卷GUID");
        redPacketHeadMap.put("status", "发送状态");
        redPacketHeadMap.put("openId", "用户openId");
        redPacketHeadMap.put("totalAmount", "红包金额");
        redPacketHeadMap.put("createTime", "创建时间");
        redPacketHeadMap.put("auditUser", "审核人");
        redPacketHeadMap.put("auditResult", "审核结果");
        redPacketHeadMap.put("auditTime", "审核时间");
        redPacketHeadMap.put("sampleId", "样本ID");
        redPacketHeadMap.put("sampleGuid", "样本GUID");
        redPacketHeadMap.put("sampleName", "样本名称");
        redPacketHeadMap.put("sampleCode", "样本编码");
        redPacketHeadMap.put("moduleId", "问卷ID");
        redPacketHeadMap.put("moduleCode", "问卷编号");
        redPacketHeadMap.put("moduleName", "问卷名称");
        redPacketHeadMap.put("questionnaireId", "项目问卷ID");
    }

    /**
     * 状态转中文
     *
     * @param status
     * @param type
     * @return
     */
    public static String status2Str(String status, String type) {
        String res = "";
        if ("SAMPLE".equals(type)) {
            switch (status) {
                case "-1":
                    res = "禁用";
                    break;
                case "0":
                    res = "初始化";
                    break;
                case "1":
                    res = "已分派";
                    break;
                case "2":
                    res = "进行中";
                    break;
                case "3":
                    res = "已完成";
                    break;
                case "4":
                    res = "拒访";
                    break;
                case "5":
                    res = "甄别";
                    break;
                case "6":
                    res = "预约";
                    break;
                case "7":
                    res = "无效号码/无法联系";
                    break;
                case "8":
                    res = "通话中";
                    break;
                case "9":
                    res = "无人接听";
                    break;
                case "10":
                    res = "审核无效";
                    break;
                case "11":
                    res = "审核回退";
                    break;
                case "12":
                    res = "审核成功";
                    break;
                case "13":
                    res = "配额溢出";
                    break;
                case "14":
                    res = "配额不满足";
                    break;
                default:
                    break;
            }
        }

        if ("RESPONSE".equals(type)) {
            switch (status) {
                case "1":
                    res = "答题中";
                    break;
                case "2":
                    res = "预约回访";
                    break;
                case "3":
                    res = "拒绝回访";
                    break;
                case "4":
                    res = "甄别失败";
                    break;
                case "5":
                    res = "不满足配额";
                    break;
                case "6":
                    res = "答题成功";
                    break;
                case "7":
                    res = "配额溢出";
                    break;
                case "8":
                    res = "一审合格";
                    break;
                case "9":
                    res = "二审合格";
                    break;
                case "10":
                    res = "终审合格";
                    break;
                case "11":
                    res = "审核无效";
                    break;
                case "12":
                    res = "审核回退";
                    break;
                default:
                    break;
            }
        }

        if ("AUDIT".equals(type)) {
            switch (status) {
                case "1":
                    res = "审核有效";
                    break;
                case "2":
                    res = "审核无效";
                    break;
                case "3":
                    res = "审核回退";
                    break;
                case "4":
                    res = "一审合格";
                    break;
                case "5":
                    res = "二审合格";
                    break;
                case "6":
                    res = "终审合格";
                    break;
                case "7":
                    res = "一审不通过";
                    break;
                case "8":
                    res = "二审不通过";
                    break;
                case "9":
                    res = "三审不通过";
                    break;
                default:
                    break;
            }
        }

        if ("REDPACKET".equals(type)) {
            switch (status) {
                case "1":
                    res = "待发送";
                    break;
                case "2":
                    res = "发送成功";
                    break;
                case "3":
                    res = "发送失败";
                    break;
                default:
                    break;
            }
        }

        return res;
    }

}
