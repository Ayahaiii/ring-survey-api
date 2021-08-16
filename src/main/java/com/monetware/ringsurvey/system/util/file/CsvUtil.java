package com.monetware.ringsurvey.system.util.file;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Cleanup;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Simo
 * @date 2020-02-28
 */
public class CsvUtil {

    /**
     * 写入csv
     *
     * @param type
     * @param properties
     * @param datas
     * @param path
     * @return
     * @throws Exception
     */
    public static Map<String, Object> createCsvFile(String type, List<String> properties, JSONArray datas, String path) throws Exception {
        Map<String, Object> res = new HashMap<>();
        Date now = new Date();
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(now) + ".csv";
        //创建csv文件
        File csvFile = new File(path + fileName);
        //获取csv文件父文件夹
        File parentFlie = csvFile.getParentFile();
        //判断父文件是否存在，不存在新建
        if (parentFlie != null && !parentFlie.exists()) {
            parentFlie.mkdirs();
        }
        csvFile.createNewFile();
        @Cleanup
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GBK"), 1024);
        StringBuffer title = new StringBuffer();
        // 写入标题
        for (int i = 0; i < properties.size(); i++) {
            switch (type) {
                case "TEAM":
                    title.append(CommonProperty.teamHeadMap.get(properties.get(i))).append(",");
                    break;
                case "SAMPLE":
                    title.append(CommonProperty.sampleHeadMap.get(properties.get(i))).append(",");
                    break;
            }
        }
        String rowStr = title.toString().substring(0, title.toString().length() - 1);
        bw.write(rowStr);
        bw.newLine();
        for (int i = 0; i < datas.size(); i++) {
            CsvUtil.writeData(properties, datas.getJSONObject(i), bw);
        }
        res.put("fileName", fileName);
        res.put("fileSize", new File(path + fileName).length());
        return res;
    }

    /**
     * 写入CSV内容
     *
     * @param properties 属性
     * @param data       内容集合
     * @param bw         写出流
     * @throws IOException
     */
    private static void writeData(List<String> properties, JSONObject data, BufferedWriter bw) throws IOException {
        StringBuffer sb = new StringBuffer();
        String rowStr = "";
        for (int i = 0; i < properties.size(); i++) {
            rowStr = sb.append(data.getString(properties.get(i))).append(",").toString();
        }
        rowStr = rowStr.substring(0, rowStr.length() - 1);
        bw.write(rowStr);
        bw.newLine();
    }

    /**
     * @param titleMap
     * @param dataList
     * @param path
     * @param fileName
     * @return
     * @throws Exception
     */
    public static Map<String, Object> createResponseCsvFile(LinkedHashMap<String, String> titleMap, List<LinkedHashMap<String, String>> dataList,
                                                            String path, String fileName) throws Exception {
        Map<String, Object> res = new HashMap<>();
        File csvFile = new File(path + fileName);
        //获取文件父文件夹
        File parentFlie = csvFile.getParentFile();
        //判断父文件是否存在，不存在新建
        if (parentFlie != null && !parentFlie.exists()) {
            parentFlie.mkdirs();
        }
        csvFile.createNewFile();

        @Cleanup
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GBK"), 1024);
        StringBuffer title = new StringBuffer();

        if (dataList != null && !dataList.isEmpty()) {
            // 标题
            for (Map.Entry<String, String> entry : titleMap.entrySet()) {
                title.append(entry.getValue()).append(",");
            }
            String rowStr = title.toString().substring(0, title.toString().length() - 1);
            bw.write(rowStr);
            bw.newLine();

            // 数据
            for (int i = 0; i < dataList.size(); i++) {
                LinkedHashMap<String, String> dataMap = dataList.get(i);
                StringBuffer sb = new StringBuffer();
                rowStr = "";
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    String content = entry.getValue();
                    if (entry.getKey().equals("responseStatus")) {
                        content = CommonProperty.status2Str(content, "RESPONSE");
                    }
                    rowStr = sb.append(content).append(",").toString();
                }
                rowStr = rowStr.substring(0, rowStr.length() - 1);
                bw.write(rowStr);
                bw.newLine();
            }
        }

        res.put("fileName", fileName);
        res.put("fileSize", new File(path + fileName).length());
        return res;
    }

}
