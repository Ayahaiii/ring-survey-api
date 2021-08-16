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
 * @author Linked
 * @date 2020/4/8 15:40
 */
@Data
public class BaseBaseSample implements IDynamicTableName {

    @Transient
    private String dynamicTableName;

    @Id
    @GeneratedValue(generator = "JDBC")
    @SQLField(column = "id", type = "int", auto = true, constraint = @Constraints(primaryKey = true))
    private Integer id;

    @SQLField(column = "name", type = "varchar", len = 255, constraint = @Constraints(allowNull = false))
    private String name;

    @SQLField(column = "gender", type = "varchar", len = 255)
    private String gender;


    /**
     * 手机号码
     */
    @SQLField(column = "telephone", type = "varchar", len = 255)
    private String telephone;

    /**
     * 手机号码
     */
    @SQLField(column = "telephone_area", type = "varchar", len = 255)
    private String telephoneArea;

    @SQLField(column = "cell_phone", type = "varchar", len = 255)
    private String cellPhone;

    @SQLField(column = "company", type = "varchar", len = 255)
    private String company;

    @SQLField(column = "address", type = "varchar", len = 255)
    private String address;

    @SQLField(column = "email", type = "varchar", len = 255)
    private String email;

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

    @SQLField(column = "custom6", type = "varchar", len = 255)
    private String custom6;

    @SQLField(column = "custom7", type = "varchar", len = 255)
    private String custom7;

    @SQLField(column = "custom8", type = "varchar", len = 255)
    private String custom8;

    @SQLField(column = "custom9", type = "varchar", len = 255)
    private String custom9;

    @SQLField(column = "custom10", type = "varchar", len = 255)
    private String custom10;

    @SQLField(column = "custom11", type = "varchar", len = 255)
    private String custom11;

    @SQLField(column = "custom12", type = "varchar", len = 255)
    private String custom12;

    @SQLField(column = "custom13", type = "varchar", len = 255)
    private String custom13;

    @SQLField(column = "custom14", type = "varchar", len = 255)
    private String custom14;

    @SQLField(column = "custom15", type = "varchar", len = 255)
    private String custom15;

    @SQLField(column = "custom16", type = "varchar", len = 255)
    private String custom16;

    @SQLField(column = "custom17", type = "varchar", len = 255)
    private String custom17;

    @SQLField(column = "custom18", type = "varchar", len = 255)
    private String custom18;

    @SQLField(column = "custom19", type = "varchar", len = 255)
    private String custom19;

    @SQLField(column = "custom20", type = "varchar", len = 255)
    private String custom20;


    @Override
    public String getDynamicTableName() {
        return null;
    }
}
