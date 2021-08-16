package com.monetware.ringsurvey.system.util.file;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Cleanup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Simo
 * @date 2020-02-28
 */
public class TxtUtil {

    /**
     * 写入csv
     * @param type
     * @param properties
     * @param datas
     * @param path
     * @return
     * @throws Exception
     */
    public static Map<String, Object> createTextFile(String type, List<String> properties, JSONArray datas, String path) throws Exception {
        Map<String, Object> res = new HashMap<>();
        Date now = new Date();
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(now) + ".txt";
        //创建text文件
        File file = new File(path + fileName);
        //获取txt文件父文件夹
        File parentFlie = file.getParentFile();
        //判断父文件是否存在，不存在新建
        if (parentFlie != null && !parentFlie.exists()) {
            parentFlie.mkdirs();
        }
        file.createNewFile();
        @Cleanup
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"), 1024);

        // 整理数据
        List<List<String>> dataList = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            List<String> temp = new ArrayList<>();
            JSONObject object = datas.getJSONObject(i);
            for (String s : properties) {
                switch (type) {
                    case "TEAM":
                        temp.add(CommonProperty.teamHeadMap.get(s) + "：" + object.getString(s));
                        break;
                    case "SAMPLE":
                        temp.add(CommonProperty.sampleHeadMap.get(s) + "：" + object.getString(s));
                        break;
                }
            }
            dataList.add(temp);
        }

        for (List<String> data : dataList) {
            for (String s : data) {
                bw.write(s);
                bw.write("\r\n");
            }
            bw.write("\r\n");
        }
        res.put("fileName", fileName);
        res.put("fileSize", new File(path + fileName).length());
        return res;
    }



}
