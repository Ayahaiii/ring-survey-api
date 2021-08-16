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
 * 短信邮件发布队列
 */
@Data
public class BaseProjectPublishQueue implements IDynamicTableName {

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

    /**
     * 类型
     */
    @SQLField(column = "type", type = "int")
    private Integer type;

    /**
     * 样本Guid
     */
    @SQLField(column = "sample_guid", type = "varchar", len = 255, index = true, constraint = @Constraints(allowNull = false))
    private String sampleGuid;

    /**
     * 接收者
     */
    @SQLField(column = "publish_to", type = "varchar", len = 255)
    private String publishTo;

    /**
     * 主题
     */
    @SQLField(column = "subject", type = "text")
    private String subject;

    /**
     * 内容
     */
    @SQLField(column = "content", type = "text")
    private String content;

    /**
     * 创建人
     */
    @SQLField(column = "create_user", type = "int")
    private Integer createUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "create_time", type = "datetime")
    private Date createTime;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "send_time", type = "datetime")
    private Date sendTime;
}
