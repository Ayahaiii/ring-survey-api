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
 * @date 2020/3/25 11:41
 */
@Data
public class BaseResponseFile implements IDynamicTableName {
    @Transient
    private String dynamicTableName;

    @Id
    @GeneratedValue(generator = "JDBC")
    @SQLField(column = "id", type = "int", auto = true, constraint = @Constraints(primaryKey = true))
    private Integer id;

    @SQLField(column = "response_guid", type = "varchar", len = 50, constraint = @Constraints(allowNull = true))
    private String responseGuid;

    @SQLField(column = "question_id", type = "varchar", len = 50, constraint = @Constraints(allowNull = true))
    private String questionId;

    @SQLField(column = "file_path", type = "varchar", len = 255, constraint = @Constraints(allowNull = false))
    private String filePath;

    @SQLField(column = "file_type", type = "varchar", len = 20, constraint = @Constraints(allowNull = true))
    private String fileType;

    @SQLField(column = "file_name", type = "varchar", len = 100, constraint = @Constraints(allowNull = true))
    private String fileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "create_time", type = "datetime", constraint = @Constraints(allowNull = true))
    private Date createTime;

    @Override
    public String getDynamicTableName() {
        return dynamicTableName;
    }
}
