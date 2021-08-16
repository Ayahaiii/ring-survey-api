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

@Data
public class BaseResponsePosition implements IDynamicTableName {

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

    @SQLField(column = "response_guid", type = "varchar", len = 255, constraint = @Constraints(allowNull = false))
    private String responseGuid;

    /**
     * 地址
     */
    @SQLField(column = "address", type = "varchar", len = 255)
    private String address;

    /**
     * 经度
     */
    @SQLField(column = "longitude", type = "varchar", len = 255)
    private String longitude;

    /**
     * 纬度
     */
    @SQLField(column = "latitude", type = "varchar", len = 255)
    private String latitude;

    /**
     * 定位时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "pos_time", type = "datetime")
    private Date posTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "create_time", type = "datetime", constraint = @Constraints(allowNull = false))
    private Date createTime;
}
