package com.monetware.ringsurvey.system.util.file;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Cleanup;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Simo
 * @date 2020-02-28
 */
public class ExcelUtil {

    public static Map<String, Object> createExcelFile(String type, List<String> properties, JSONArray datas, String path) throws Exception {
        Map<String, Object> res = new HashMap<>();
        Date now = new Date();
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(now) + ".xls";
        File file = new File(path + fileName);
        //获取文件父文件夹
        File parentFlie = file.getParentFile();
        //判断父文件是否存在，不存在新建
        if (parentFlie != null && !parentFlie.exists()) {
            parentFlie.mkdirs();
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();

        //设置第一行写入标题
        HSSFRow row = sheet.createRow(0);
        String title = "";
        for (int i = 0; i < properties.size(); i++) {
            switch (type) {
                case "TEAM":
                    title = CommonProperty.teamHeadMap.get(properties.get(i));
                    break;
                case "SAMPLE":
                    title = CommonProperty.sampleHeadMap.get(properties.get(i));
                    break;
                case "REDPACKET":
                    title = CommonProperty.redPacketHeadMap.get(properties.get(i));
                    break;
            }
            row.createCell(i).setCellValue(title);
        }

        // 写入数据
        for (int i = 0; i < datas.size(); i++) {
            JSONObject obj = datas.getJSONObject(i);
            row = sheet.createRow(i + 1);
            for (int j = 0; j < properties.size(); j++) {
                String cellValue = obj.getString(properties.get(j));
                if ("REDPACKET".equals(type)) {
                    if (properties.get(j).equals("status")) {
                        cellValue = CommonProperty.status2Str(cellValue, "REDPACKET");
                    }
                }
                row.createCell(j).setCellValue(cellValue);
            }
        }

        //文档输出
        @Cleanup
        FileOutputStream out = new FileOutputStream(path + fileName);
        workbook.write(out);
        res.put("fileName", fileName);
        res.put("fileSize", new File(path + fileName).length());
        return res;
    }

    public static Map<String, Object> createResponseExcelFile(LinkedHashMap<String, String> titleMap, Map<String, String> titleTypeMap,
                                                              List<LinkedHashMap<String, String>> dataList, String path, String fileName) throws Exception {
        Map<String, Object> res = new HashMap<>();
        File file = new File(path + fileName);
        //获取文件父文件夹
        File parentFlie = file.getParentFile();
        //判断父文件是否存在，不存在新建
        if (parentFlie != null && !parentFlie.exists()) {
            parentFlie.mkdirs();
        }

        SXSSFWorkbook xssf_w_book = new SXSSFWorkbook(100);
        SXSSFSheet xssf_w_sheet = null;
        Row xssf_w_row = null;// 创建一行
        Cell xssf_w_cell = null;// 创建每个单元格

        CellStyle head_cellStyle = xssf_w_book.createCellStyle();// 创建一个单元格样式
        Font head_font = xssf_w_book.createFont();
        head_font.setFontName("宋体");// 设置头部字体为宋体
        head_font.setBold(true); // 粗体
        head_font.setFontHeightInPoints((short) 10);
        head_cellStyle.setFont(head_font);// 单元格样式使用字体
        head_cellStyle.setWrapText(true);
        head_cellStyle.setBorderBottom(BorderStyle.THIN);
        head_cellStyle.setBorderLeft(BorderStyle.THIN);
        head_cellStyle.setBorderRight(BorderStyle.THIN);
        head_cellStyle.setBorderTop(BorderStyle.THIN);
        DataFormat head_format = xssf_w_book.createDataFormat();
        head_cellStyle.setDataFormat(head_format.getFormat("@"));
        CellStyle cellStyle_CN = xssf_w_book.createCellStyle();// 创建数据单元格样式(数据库数据样式)
        cellStyle_CN.setBorderBottom(BorderStyle.NONE);
        cellStyle_CN.setBorderLeft(BorderStyle.NONE);
        cellStyle_CN.setBorderRight(BorderStyle.NONE);
        cellStyle_CN.setBorderTop(BorderStyle.NONE);

        int rowIndex = 0;
        // 标题行
        xssf_w_sheet = (SXSSFSheet) xssf_w_book.createSheet(fileName);
        xssf_w_sheet.createFreezePane(0, 1, 0, 1); // 冻结窗口
        xssf_w_sheet.setDefaultColumnWidth(12);

        int i = 0; // 列索引
        xssf_w_row = xssf_w_sheet.createRow(0 + rowIndex);// 第一行写入标题行
        xssf_w_row.setHeight((short) (2 * 256));
        for (Map.Entry<String, String> entry : titleMap.entrySet()) {
            Object key = entry.getValue(); // 标题
            if (key == null) {
                key = "";
            }
            xssf_w_cell = xssf_w_row.createCell(i);
            xssf_w_cell.setCellType(CellType.STRING);
            XSSFRichTextString xssfString = new XSSFRichTextString(key.toString());
            xssf_w_cell.setCellValue(xssfString);
            xssf_w_cell.setCellStyle(head_cellStyle);
            xssf_w_sheet.trackAllColumnsForAutoSizing();
            if (xssfString.toString().indexOf(".") >= 0) {
                xssf_w_sheet.setColumnWidth(i, 30 * 256);
            } else {
                xssf_w_sheet.autoSizeColumn((short) i);
            }
            i++;
        }

        // 数据内容
        for (int j = 0; j < dataList.size(); j++) {
            LinkedHashMap<String, String> responseMap = dataList.get(j);
            int z = 0; // 列数
            rowIndex++; // 数据从第二行开始
            xssf_w_row = xssf_w_sheet.createRow(rowIndex);
            xssf_w_row.setHeight((short) (1.5 * 256));
            for (Map.Entry<String, String> entry : titleMap.entrySet()) {
                Object value = responseMap.get(entry.getKey()); // 数据
                String content = (value == null ? "" : value.toString());
                content = content.replace("null", "");
                content = content.replace("\n", "");

                if (entry.getKey().equals("responseStatus")) {
                    content = CommonProperty.status2Str(content, "RESPONSE");
                }
                xssf_w_cell = xssf_w_row.createCell((short) z);
                XSSFRichTextString xssfString = new XSSFRichTextString(String.valueOf(content));
                if (null != titleTypeMap.get(entry.getKey()) && "CELL_TYPE_NUMERIC".equals(titleTypeMap.get(entry.getKey()))) {
                    xssf_w_cell.setCellType(CellType.NUMERIC);
                    if (StringUtils.isNotBlank(xssfString.toString())) {
                        xssf_w_cell.setCellValue(Double.valueOf(xssfString.toString()));
                    }
                } else {
                    xssf_w_cell.setCellType(CellType.STRING);
                    xssf_w_cell.setCellValue(xssfString.toString());
                }
                xssf_w_cell.setCellStyle(cellStyle_CN);
                z++;
            }
        }

        //文档输出
        @Cleanup
        FileOutputStream out = new FileOutputStream(path + fileName);
        xssf_w_book.write(out);
        res.put("fileName", fileName);
        res.put("fileSize", new File(path + fileName).length());
        return res;
    }

}
