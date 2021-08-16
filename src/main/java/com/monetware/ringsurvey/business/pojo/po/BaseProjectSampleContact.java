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
 * 样本更多联系方式表（动态）
 */
@Data
public class BaseProjectSampleContact implements IDynamicTableName {

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

    @SQLField(column = "sample_guid", type = "varchar", len = 255, constraint = @Constraints(allowNull = false))
    private String sampleGuid;

    @SQLField(column = "name", type = "varchar", len = 255)
    private String name;

    /**
     * 关系
     */
    @SQLField(column = "relation", type = "varchar", len = 255)
    private String relation;

    /**
     * 备注
     */
    @SQLField(column = "description", type = "varchar", len = 255)
    private String description;

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
    @SQLField(column = "is_delete", type = "int", constraint = @Constraints(allowNull = false), defaultValue = "0")
    private Integer isDelete;

    /**
     * 删除用户
     */
    @SQLField(column = "delete_user", type = "int")
    private Integer deleteUser;

}
