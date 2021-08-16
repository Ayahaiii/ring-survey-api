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
 * @date 2020/2/18 10:44
 * 样本分派表（动态）
 */
@Data
public class BaseSampleAssignment implements IDynamicTableName {

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

    @SQLField(column = "team_user_id", type = "int", constraint = @Constraints(allowNull = false))
    private Integer teamUserId;

    /**
     * 1:负责人，2:协作者
     */
    @SQLField(column = "type", type = "int", constraint = @Constraints(allowNull = false))
    private Integer type;

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
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "last_modify_time", type = "datetime", constraint = @Constraints(allowNull = false))
    private Date lastModifyTime;

    /**
     * 修改人
     */
    @SQLField(column = "last_modify_user", type = "int", constraint = @Constraints(allowNull = false))
    private Integer lastModifyUser;

    /**
     * 是否删除
     */
    @SQLField(column = "is_delete", type = "int", defaultValue = "0")
    private Integer isDelete;

    /**
     * 删除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "delete_time", type = "datetime")
    private Date deleteTime;

    /**
     * 删除人
     */
    @SQLField(column = "delete_user", type = "int")
    private Integer deleteUser;

}
