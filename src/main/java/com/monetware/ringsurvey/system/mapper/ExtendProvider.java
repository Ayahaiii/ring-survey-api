package com.monetware.ringsurvey.system.mapper;

import com.monetware.ringsurvey.system.mapper.annotation.SQLField;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;

import java.lang.reflect.Field;

/**
 * @author Simo
 * @date 2019-06-10
 */
public class ExtendProvider extends MapperTemplate {

    public ExtendProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String createCustomTable(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        Field[] fields = entityClass.getDeclaredFields();
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ${tableName} ( \n");
        for (int i = 0; i < fields.length; i++) {
            SQLField ff = fields[i].getAnnotation(SQLField.class);
            if(ff != null)
            {
                if (ff.index()) {
                    sql.append(" INDEX " + ff.column() + " ( " + ff.column() + " ) , ");
                }
                sql.append(" `" + ff.column() + "` ");
                sql.append(" " + ff.type());
                if (ff.len() != 0) {
                    sql.append("(" + ff.len() + ") ");
                }
                if (ff.auto()) {
                    sql.append(" AUTO_INCREMENT ");
                }
                if (!ff.constraint().allowNull()) {
                    sql.append(" NOT NULL ");
                }
                if (ff.constraint().primaryKey()) {
                    sql.append(" PRIMARY KEY ");
                }
                if (!"".equals(ff.defaultValue())) {
                    sql.append(" DEFAULT '" + ff.defaultValue() + "'");
                }
                if (i != fields.length - 1) {
                    sql.append(",\n");
                }
            }
        }
        sql.append("\n ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
        return sql.toString();
    }

    public String dropCustomTable(MappedStatement ms) {
        StringBuilder sql = new StringBuilder();
        sql.append("DROP TABLE IF EXISTS ${tableName} ");
        return sql.toString();
    }

}
