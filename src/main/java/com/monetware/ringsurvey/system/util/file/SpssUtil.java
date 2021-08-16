package com.monetware.ringsurvey.system.util.file;

import com.monetware.ringsurvey.business.pojo.constants.ResponseConstants;
import com.pmstation.spss.SPSSWriter;
import com.pmstation.spss.ValueLabels;
import lombok.Cleanup;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpssUtil {

    public static Map<String, Object> createResponseSpssFile(LinkedHashMap<String, String> titleMap, List<LinkedHashMap<String, String>> dataMapList,
                                                             Map<String, List<String>> optionMap, int optionType, String path, String fileName) throws Exception {
        Map<String, Object> res = new HashMap<>();
        File savFile = new File(path + fileName);
        //获取文件父文件夹
        File parentFlie = savFile.getParentFile();
        //判断父文件是否存在，不存在新建
        if (parentFlie != null && !parentFlie.exists()) {
            parentFlie.mkdirs();
        }

        @Cleanup
        OutputStream out = new FileOutputStream(savFile);
        SPSSWriter outSPSS = new SPSSWriter(out, "utf8");

        outSPSS.setCalculateNumberOfCases(false);
        outSPSS.addDictionarySection(-1);
        int count = 1;
        for (Map.Entry<String, String> entry : titleMap.entrySet()) {
            String var = entry.getKey();
            String label = entry.getValue();

            // 设置值标签
            if (null != optionMap.get(entry.getKey()) && optionType == ResponseConstants.EXPORT_ID) {// 导出编号时
                outSPSS.addNumericVar(var, 8, 0, label, null);
                List<String> optionList = optionMap.get(entry.getKey());
                ValueLabels valueLabels = new ValueLabels();
                for (int i = 0; i < optionList.size(); i++) {
                    String optionStr = optionList.get(i);
                    String[] strings = optionStr.split("=");
                    valueLabels.putLabel(Double.parseDouble(strings[0]), strings[1]);
                }
                outSPSS.addValueLabels(count, valueLabels);
            } else {
                outSPSS.addStringVar(var, 255, label);
            }
            count++;
        }

        // 内容(SPSS的数据视图)
        outSPSS.addDataSection();
        if (dataMapList != null && !dataMapList.isEmpty()) {
            for (LinkedHashMap<String, String> exportMap : dataMapList) {
                for (Map.Entry<String, String> entry : titleMap.entrySet()) {
                    String value = exportMap.get(entry.getKey());
                    String content = (value == null ? "" : value);
                    content = content.replace("null", "");
                    content = content.replace("\n", "");
                    if (null != optionMap.get(entry.getKey()) && optionType == ResponseConstants.EXPORT_ID) {// 导出编号时
                        if (StringUtils.isNotBlank(content)) {
                            outSPSS.addData(new Long(content));
                        } else {
                            outSPSS.addData((Double) null);
                        }
                    } else {
                        if (entry.getKey().equals("responseStatus")) {
                            content = CommonProperty.status2Str(content, "RESPONSE");
                        }
                        outSPSS.addData(content);
                    }
                }
            }
        }
        outSPSS.addFinishSection();

        res.put("fileName", fileName);
        res.put("fileSize", new File(path + fileName).length());
        return res;
    }

}
