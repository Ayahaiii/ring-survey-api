package com.monetware.ringsurvey.system.util.file;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.monetware.ringsurvey.business.pojo.vo.analyzer.AnalysisPdfVO;
import lombok.Cleanup;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Linked
 * @date 2020/5/13 9:50
 */
@Component
public class Base64ToFileUtil {

    private final String RADIO_BOX = " \u25cb  ";

    private final String RADIO_BOX1 = " \u25cf  ";

    private final String CHECK_BOX = " \u25a1  ";

    /**
     * EXPORT PDF
     *
     * @param obj
     * @param response
     * @throws Exception
     */
    public void createPDF(Object obj, HttpServletResponse response) throws Exception {
        AnalysisPdfVO analysisPdfVO = null;
        String content = "";
        String title = "";
        String waterStr = "";
        if (obj instanceof AnalysisPdfVO) {
            analysisPdfVO = (AnalysisPdfVO) obj;
            title = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            waterStr = "锐研·云调查";
        }
        String fileName = URLEncoder.encode(title, "UTF-8") + ".pdf";
        //CHINESE FONT
        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font font16 = new Font(bfChinese, 16, Font.BOLD);
        Font font10 = new Font(bfChinese, 10);
        //CREATE ONE A4 PAGE
        Document document = new Document(PageSize.A4, 30, 30, 40, 40);
        //EXPORT PDF TO LOCAL
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");
        @Cleanup
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        //CREATE HEADER AND FOOTER
        createHeaderAndFooter(writer, waterStr, bfChinese);
        //OPEN PDF PAGE
        document.open();
        //WRITE CONTENT IN HEADER
        //writeHeadContent(document, title, font16);
        //WRITE POLICY CONTENT IN BODY
        if (analysisPdfVO != null) {
            writeAnalyzeResult(document, font10, analysisPdfVO);
        }
        //CLOSE PDF PAGE
        document.close();
    }

    /**
     * CREATE HEADER AND FOOTER
     *
     * @param writer
     */
    private void createHeaderAndFooter(PdfWriter writer, String waterStr, BaseFont bfChinese) {
        writer.setPageEvent(new PdfPageEventHelper() {
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContent();
                PdfGState gs = new PdfGState();
                // 设置透明度为0.3
                gs.setFillOpacity(0.3f);
                cb.setGState(gs);
                Phrase watermark = new Phrase(waterStr, new Font(bfChinese, 30, Font.NORMAL, BaseColor.BLACK));
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, watermark, 298, 421, 45);
                cb.saveState();
                cb.beginText();
                BaseFont bf = null;
                try {
                    bf = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cb.setFontAndSize(bf, 10);

                //Header
//                float x = document.top(-20);
//
//                if (writer.getCurrentPageNumber() != 1){
//                    //左
//                    cb.showTextAligned(PdfContentByte.ALIGN_LEFT, titleStr,
//                            document.left(), x, 0);
//                }
//                //中
//                cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
//                        "上海萌泰数据科技股份有限公司",
//                        (document.right() + document.left())/2,
//                        x, 0);
//                //右
//                cb.showTextAligned(PdfContentByte.ALIGN_RIGHT,
//                        "H-Right",
//                        document.right(), x, 0);
//                try {
//                    Image image = Image.getInstance("/Users/monetware/Desktop/1.png");
//                    image.scaleAbsolute(100, 20);
//                    cb.addImage(image);
//                } catch (Exception e){
//                    e.printStackTrace();
//                }

                //Footer
                float y = document.bottom(-20);

//                //左
//                cb.showTextAligned(PdfContentByte.ALIGN_LEFT,
//                        "F-Left",
//                        document.left(), y, 0);
                //中
                cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
                        "第" + writer.getCurrentPageNumber() + "页",
                        (document.right() + document.left()) / 2,
                        y, 0);
//                //右
//                cb.showTextAligned(PdfContentByte.ALIGN_RIGHT,
//                        writer.getCurrentPageNumber()+"",
//                        document.right(), y, 0);
                cb.endText();
                cb.restoreState();
            }
        });
    }

    /**
     * WRITE CONTENT IN HEADER
     *
     * @param document
     * @param title
     * @param font
     * @throws Exception
     */
    private void writeHeadContent(Document document, String title, Font font) throws Exception {
        document.add(new Paragraph("\n"));
        Paragraph p = new Paragraph(title, font);
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph("\n"));
    }

    /**
     * WRITE POLICY CONTENT IN BODY
     *
     * @param document
     * @param font
     * @param analysisPdfVO
     * @throws Exception
     */
    private void writeAnalyzeResult(Document document, Font font, AnalysisPdfVO analysisPdfVO) throws Exception {
        if (analysisPdfVO.getImgStrs() != null) {
            StringBuffer imgStr = new StringBuffer();
            for (int i = 0; i < analysisPdfVO.getImgStrs().size(); i++) {
                for (int j = 0 ;j<analysisPdfVO.getImgStrs().get(i).size() ;j++) {
                    String s = analysisPdfVO.getImgStrs().get(i).get(j);
                    if (j == 0) {
                        imgStr.append(s.substring(s.indexOf("base64,") + 7));
                    } else {
                        imgStr.append(s);
                    }
                }
                byte[] imgData = Base64.decodeBase64(imgStr.toString());
                Image img = Image.getInstance(imgData);
                img.setAlignment(Image.MIDDLE);
                float h = img.getHeight();
                float w = img.getWidth();
                int percent2 = getPercent2(h, w);
                img.scalePercent(percent2);
                document.add(img);
                imgStr = new StringBuffer();
            }
        }
        document.add(new Paragraph("\n"));
    }

    /**
     * ADD WATER STAMPER
     *
     * @param writer
     * @param bfChinese
     * @param waterStr
     * @throws Exception
     */
    private void writeStamper(PdfWriter writer, BaseFont bfChinese, String waterStr) {
        writer.setPageEvent(new PdfPageEventHelper() {
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContent();
                cb.saveState();
                cb.beginText();
                PdfGState gs = new PdfGState();
                // 设置透明度为0.3
                gs.setFillOpacity(0.3f);
                cb.setGState(gs);
                Phrase watermark = new Phrase(waterStr, new Font(bfChinese, 30, Font.NORMAL, BaseColor.BLACK));
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, watermark, 298, 421, 45);
                cb.endText();
                cb.restoreState();
            }
        });
    }

    /**
     * CALCULATE PERCENTAGE
     *
     * @param h
     * @param w
     * @return
     */
    private int getPercent2(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        p2 = 530 / w * 100;
        p = Math.round(p2);
        return p;
    }

    /**
     * CONTACT STRING
     *
     * @param list
     * @return
     */
    private String getContactStr(List<String> list) {
        String res = "";
        if (list != null) {
            for (String s : list) {
                res += s + "  ";
            }
        }
        return res;
    }

}
