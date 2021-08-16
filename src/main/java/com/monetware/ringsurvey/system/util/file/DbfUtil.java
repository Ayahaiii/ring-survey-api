package com.monetware.ringsurvey.system.util.file;

import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import lombok.Cleanup;
import sun.nio.cs.ext.GBK;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class DbfUtil {

    public static Map<String, Object> createResponseDbfFile(LinkedHashMap<String, String> titleMap, Map<String, String> titleTypeMap,
                                                            List<LinkedHashMap<String, String>> dataList, String path, String fileName) throws Exception {
        Map<String, Object> res = new HashMap<>();
        File dbfFile = new File(path + fileName);
        //获取文件父文件夹
        File parentFlie = dbfFile.getParentFile();
        //判断父文件是否存在，不存在新建
        if (parentFlie != null && !parentFlie.exists()) {
            parentFlie.mkdirs();
        }

        @Cleanup
        DBFWriter dbfWriter = new DBFWriter(new FileOutputStream(dbfFile), new GBK());
        dbfWriter.setCharactersetName("GBK");

        List<String> headersList = new ArrayList<>();
        List<String> contentList = new ArrayList<>();
        DBFField[] headers = new DBFField[titleMap.size()];
        String[] content = new String[titleMap.size()];
        //表头的信息
        for (Map.Entry<String, String> entry : titleMap.entrySet()) {
            headersList.add(entry.getValue());
        }
        for (int i = 0; i < headersList.size(); i++) {
            headers[i] = new DBFField();
            headers[i].setName(" ");
            headers[i].setDataType(DBFField.FIELD_TYPE_C);
            headers[i].setLength(10);
        }
        // 添加全为空字符串的表头
        dbfWriter.setFields(headers);

        // 将表头作为第一行插入
        dbfWriter.addRecord(headersList.toArray());

        // 添加每个单元格的内容
        if (dataList != null && !dataList.isEmpty()) {
            for (LinkedHashMap<String, String> map : dataList) {
                for (Map.Entry<String, String> entry : titleMap.entrySet()) {
                    String value = map.get(entry.getKey());
                    if (null == value || "null".equals(value)) {
                        contentList.add(" ");
                    } else {
                        if (entry.getKey().equals("responseStatus")) {
                            value = CommonProperty.status2Str(value, "RESPONSE");
                        }
                        contentList.add(value);
                    }
                }
                content = new String[titleMap.size()];
                for (int i = 0; i < contentList.size(); i++) {
                    content[i] = (contentList.get(i));
                }
                dbfWriter.addRecord(content);
                contentList.clear();
            }
        }

        res.put("fileName", fileName);
        res.put("fileSize", new File(path + fileName).length());
        return res;
    }

}
