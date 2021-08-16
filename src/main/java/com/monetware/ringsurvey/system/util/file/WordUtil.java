package com.monetware.ringsurvey.system.util.file;

import lombok.Cleanup;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author Simo
 * @date 2020-04-02
 */
public class WordUtil {


    /**
     * 导出统计word
     * @param fileName
     * @param datas
     * @param response
     * @throws Exception
     */
    public static void createWordFile(String fileName, List<List<Map<String, Object>>> datas, HttpServletResponse response) throws Exception {
        fileName = URLEncoder.encode(fileName, "UTF-8") + ".docx" ;
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");

        XWPFDocument doc = new XWPFDocument();

        for (List<Map<String, Object>> data : datas) {

            for (Map<String, Object> temp : data) {
                if ("img".equals(temp.get("type"))) {
                    List<String> contents = (List<String>) temp.get("content");
                    StringBuffer imgStr = new StringBuffer();
                    for (int i = 0; i < contents.size(); i++) {
                        String s = contents.get(i);
                        if (i == 0) {
                            imgStr.append(s.substring(s.indexOf("base64,") + 7));
                        } else {
                            imgStr.append(s);
                        }
                    }
                    byte[] imgData = Base64.decodeBase64(imgStr.toString());
                    @Cleanup
                    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(imgData);
                    XWPFParagraph picture = doc.createParagraph();
                    XWPFRun pictureContent = picture.createRun();
                    pictureContent.addPicture(byteInputStream, XWPFDocument.PICTURE_TYPE_JPEG, "", Units.toEMU(800 / 2),
                            Units.toEMU(500 / 2)); // 200x200 pixels
                } else {
                    XWPFParagraph title = doc.createParagraph();
                    title.setAlignment(ParagraphAlignment.CENTER);
                    title.setVerticalAlignment(TextAlignment.TOP);
                    XWPFRun titleContent = title.createRun();
                    titleContent.setBold(true);
                    titleContent.setText(temp.get("content").toString());
                    titleContent.setFontFamily("宋体");
                    titleContent.setTextPosition(20);
                }
            }

            // 换页
            XWPFParagraph breakLine = doc.createParagraph();
            XWPFRun breakLineContent = breakLine.createRun();
            breakLineContent.addBreak(BreakType.PAGE);

        }

        @Cleanup
        OutputStream out = new BufferedOutputStream(response.getOutputStream());
        doc.write(out);

    }


}
