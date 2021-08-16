package com.monetware.ringsurvey.business.pojo.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monetware.ringsurvey.system.mapper.annotation.Constraints;
import com.monetware.ringsurvey.system.mapper.annotation.SQLField;
import lombok.Data;
import tk.mybatis.mapper.entity.IDynamicTableName;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author Simo
 * @date 2020-03-03
 * 样本表（动态）
 */
@Data
public class BaseProjectSample implements IDynamicTableName {

    @Transient
    private String dynamicTableName;

    @Override
    public String getDynamicTableName() {
        return dynamicTableName;
    }

    @Id
    @GeneratedValue(generator = "JDBC")
    @SQLField(column = "id", type = "int", auto = true, constraint = @Constraints(primaryKey = true))
    private Integer id;

    @SQLField(column = "name", type = "varchar", len = 255)
    private String name;

    @SQLField(column = "sample_guid", type = "varchar", len = 255, index = true, constraint = @Constraints(allowNull = false))
    private String sampleGuid;

    /**
     * 是否虚拟样本
     */
    @SQLField(column = "if_virtual", type = "int", constraint = @Constraints(allowNull = false))
    private Integer ifVirtual;

    /**
     * 编号
     */
    @SQLField(column = "code", type = "varchar", len = 255, constraint = @Constraints(allowNull = false))
    private String code;

    @SQLField(column = "gender", type = "varchar", len = 255)
    private String gender;

    @SQLField(column = "age", type = "int")
    private Integer age;

    /**
     * 出生日期
     */
    @SQLField(column = "birth", type = "varchar", len = 255)
    private String birth;

    /**
     * 婚姻状况
     */
    @SQLField(column = "marriage_status", type = "varchar", len = 255)
    private String marriageStatus;

    /**
     * 学历
     */
    @SQLField(column = "education", type = "varchar", len = 255)
    private String education;

    /**
     * 单位
     */
    @SQLField(column = "organization", type = "varchar", len = 255)
    private String organization;

    /**
     * 职业
     */
    @SQLField(column = "profession", type = "varchar", len = 255)
    private String profession;

    /**
     * 职务
     */
    @SQLField(column = "position", type = "varchar", len = 255)
    private String position;

    /**
     * 政治面貌
     */
    @SQLField(column = "political_status", type = "varchar", len = 255)
    private String politicalStatus;

    /**
     * 宗教
     */
    @SQLField(column = "religion", type = "varchar", len = 255)
    private String religion;

    /**
     * 国籍
     */
    @SQLField(column = "nationality", type = "varchar", len = 255)
    private String nationality;

    /**
     * 语言
     */
    @SQLField(column = "language", type = "varchar", len = 255)
    private String language;

    /**
     * 籍贯
     */
    @SQLField(column = "place_of_birth", type = "varchar", len = 255)
    private String placeOfBirth;

    /**
     * 方言
     */
    @SQLField(column = "dialects", type = "varchar", len = 255)
    private String dialects;

    /**
     * 备注
     */
    @SQLField(column = "description", type = "varchar", len = 255)
    private String description;

    /**
     * 详细介绍
     */
    @SQLField(column = "detail", type = "text")
    private String detail;

    /**
     * 邮箱
     */
    @SQLField(column = "email", type = "varchar", len = 255)
    private String email;

    /**
     * 电话号码
     */
    @SQLField(column = "mobile", type = "varchar", len = 255)
    private String mobile;

    /**
     * 手机号码
     */
    @SQLField(column = "phone", type = "varchar", len = 255)
    private String phone;

    @SQLField(column = "weixin", type = "varchar", len = 255)
    private String weixin;

    @SQLField(column = "qq", type = "varchar", len = 255)
    private String qq;

    @SQLField(column = "weibo", type = "varchar", len = 255)
    private String weibo;

    /**
     * 省
     */
    @SQLField(column = "province", type = "varchar", len = 255)
    private String province;

    /**
     * 市
     */
    @SQLField(column = "city", type = "varchar", len = 255)
    private String city;

    /**
     * 区
     */
    @SQLField(column = "district", type = "varchar", len = 255)
    private String district;

    /**
     * 镇
     */
    @SQLField(column = "town", type = "varchar", len = 255)
    private String town;

    /**
     * 详细地址
     */
    @SQLField(column = "address", type = "varchar", len = 255)
    private String address;

    /**
     * 自定义
     */
    @SQLField(column = "custom1", type = "varchar", len = 255)
    private String custom1;

    @SQLField(column = "custom2", type = "varchar", len = 255)
    private String custom2;

    @SQLField(column = "custom3", type = "varchar", len = 255)
    private String custom3;

    @SQLField(column = "custom4", type = "varchar", len = 255)
    private String custom4;

    @SQLField(column = "custom5", type = "varchar", len = 255)
    private String custom5;

    /**
     * 经度
     */
    @SQLField(column = "lon", type = "varchar", len = 255)
    private String lon;

    /**
     * 纬度
     */
    @SQLField(column = "lat", type = "varchar", len = 255)
    private String lat;

    /**
     * 回收次数
     */
    @SQLField(column = "recycle_times", type = "int", defaultValue = "0")
    private Integer recycleTimes;

    /**
     * 接触次数
     */
    @SQLField(column = "contact_times", type = "int", defaultValue = "0")
    private Integer contactTimes;

    /**
     * 短信发送次数
     */
    @SQLField(column = "sms_times", type = "int", defaultValue = "0")
    private Integer smsTimes;

    /**
     * 邮件发送次数
     */
    @SQLField(column = "email_times", type = "int", defaultValue = "0")
    private Integer emailTimes;

    /**
     * 数据数（用于电话调查）
     */
    @SQLField(column = "random_number", type = "int")
    private Integer randomNumber;

    /**
     * 样本状态
     * 0:初始化，1:已分配，2:进行中 3:已完成
     */
    @SQLField(column = "status", type = "int", constraint = @Constraints(allowNull = false))
    private Integer status;

    /**
     * 问卷密码
     */
    @SQLField(column = "password", type = "varchar", len = 255)
    private String password;

    /**
     * 答卷变量
     */
    @SQLField(column = "response_variable", type = "longtext")
    private String responseVariable;

    /**
     * 附加数据
     */
    @SQLField(column = "extra_data", type = "longtext")
    private String extraData;

    /**
     * 预约回访时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "appoint_visit_time", type = "datetime")
    private Date appointVisitTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "create_time", type = "datetime", constraint = @Constraints(allowNull = false))
    private Date createTime;

    /**
     * 创建人
     */
    @SQLField(column = "create_user", type = "int", constraint = @Constraints(allowNull = false))
    private Integer createUser;

    /**
     * 最后修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "last_modify_time", type = "datetime", constraint = @Constraints(allowNull = false))
    private Date lastModifyTime;

    /**
     * 最后修改用户
     */
    @SQLField(column = "last_modify_user", type = "int", constraint = @Constraints(allowNull = false))
    private Integer lastModifyUser;

    /**
     * 是否删除
     */
    @SQLField(column = "is_delete", type = "int", defaultValue = "0")
    private Integer isDelete;

    /**
     * 删除用户
     */
    @SQLField(column = "delete_user", type = "int")
    private Integer deleteUser;

}
