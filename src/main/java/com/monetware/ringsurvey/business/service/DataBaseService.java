package com.monetware.ringsurvey.business.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Service
public class DataBaseService {

    @Resource
    JdbcTemplate jdbcTemplate;

    /**
     * 判断数据表是否存在
     *
     * @param tableName
     * @return
     */
    public boolean validateTableExist(String tableName) {
        Connection conn = null;
        ResultSet tabs = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData dbMetaData = conn.getMetaData();
            String[] types = {"TABLE"};
            tabs = dbMetaData.getTables(null, null, tableName, types);
            if (tabs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                tabs.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
