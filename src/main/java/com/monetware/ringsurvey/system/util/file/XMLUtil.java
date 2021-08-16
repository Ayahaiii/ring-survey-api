package com.monetware.ringsurvey.system.util.file;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

public class XMLUtil {

    public static void createQuestionnaireXML(String fileName, String xmlContent, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        response.setHeader("Charset", "utf-8");
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String((fileName + ".xml").getBytes(), "ISO-8859-1"));
        byte[] osBytes = xmlContent.getBytes("utf-8");
        response.addHeader("Content-Length", "" + osBytes.length);
        response.setContentType("application/octet-stream");
        OutputStream os = response.getOutputStream();
        os.write(osBytes);
        os.flush();
        os.close();
    }
}
