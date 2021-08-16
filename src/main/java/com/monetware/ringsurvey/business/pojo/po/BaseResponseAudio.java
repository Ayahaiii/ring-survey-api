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
 * @date 2020/3/25 10:58
 */
@Data
public class BaseResponseAudio implements IDynamicTableName {

    @Transient
    private String dynamicTableName;

    @Id
    @GeneratedValue(generator = "JDBC")
    @SQLField(column = "id", type = "int", auto = true, constraint = @Constraints(primaryKey = true))
    private Integer id;

    @SQLField(column = "response_guid", type = "varchar", len = 255, constraint = @Constraints(allowNull = false))
    private String responseGuid;


    @SQLField(column = "audio_name", type = "varchar", len = 255, constraint = @Constraints(allowNull = false))
    private String audioName;


    @SQLField(column = "file_path", type = "varchar", len = 255, constraint = @Constraints(allowNull = false))
    private String filePath;

    /**
     * 录音开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "start_time", type = "datetime", constraint = @Constraints(allowNull = true))
    private Date startTime;

    /**
     * 录音结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "end_time", type = "datetime", constraint = @Constraints(allowNull = true))
    private Date endTime;

    /**
     * 录音时长
     */
    @SQLField(column = "audio_duration", type = "int", len = 11, constraint = @Constraints(allowNull = true))
    private Integer audioDuration;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "create_time", type = "datetime", constraint = @Constraints(allowNull = true))
    private Date createTime;

    /**
     * 访问员id
     */
    @SQLField(column = "interview_id", type = "int", len = 11, constraint = @Constraints(allowNull = true))
    private Integer interviewId;

    /**
     * 录音类型 1:答卷录音 2:单题录音
     */
    @SQLField(column = "type", type = "int", len = 11, constraint = @Constraints(allowNull = false))
    private Integer type;

    /**
     * 题目id
     */
    @SQLField(column = "question_id", type = "varchar", len = 50, constraint = @Constraints(allowNull = true))
    private String questionId;

    /**
     * 单题录音结束时间点
     */
    @SQLField(column = "begin_pos", type = "int", len = 11, constraint = @Constraints(allowNull = true))
    private Integer beginPos;

    /**
     * 单题录音开始时间点
     */
    @SQLField(column = "end_pos", type = "int", len = 11, constraint = @Constraints(allowNull = true))
    private Integer endPos;


    @Override
    public String getDynamicTableName() {
        return dynamicTableName;
    }

}
