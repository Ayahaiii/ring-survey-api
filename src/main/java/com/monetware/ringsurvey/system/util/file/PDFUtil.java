package com.monetware.ringsurvey.system.util.file;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.monetware.ringsurvey.survml.questions.*;
import com.monetware.ringsurvey.survml.questions.ListItem;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Simo
 * @date 2018/12/29 15:00
 */
public class PDFUtil {

    private static final String RADIO_BOX = "\u25cb  ";

    private static final String CHECK_BOX = "\u25a1  ";

    public static void createQuestionnairePDF(String title, List<Question> questionList, List<Page> pages,
                                              Integer type, String logoUrl, HttpServletResponse response) throws Exception {
        String fileName = URLEncoder.encode(title, "UTF-8") + ".pdf";
        //中文字体
        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        //创建一个A4纸的PDF
        Document document = new Document(PageSize.A4, 30, 30, 40, 40);
        //写出到指定路径
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        //创建页眉和页脚
        createHeaderAndFooter(writer, title);
        //打开PDF页面
        document.open();
        //写入头部内容
        writeHeadContent(document, bfChinese, title, logoUrl);
        //写入问卷内容
        int count = 0;
        for (Question question : questionList) {
//            writeQuestionContent(document, bfChinese, question, type);
            writeQuestionContentNew(document, bfChinese, question, type);
            count++;
            if (type == 2 && count != questionList.size()) {
                for (Page page : pages) {
                    if (page.getEnd().equals(question.getId())) {
                        Paragraph p = new Paragraph(new Chunk(new DottedLineSeparator()));
                        p.setFont(new Font(bfChinese, 6));
                        document.add(p);
                        document.add(new Paragraph("\n"));
                        break;
                    }
                }
            }
        }
        //写入菜单
        writePageContent(writer, questionList, title);
        //关闭流
        document.close();
    }

    private static void createHeaderAndFooter(PdfWriter writer, String titleStr) {
        writer.setPageEvent(new PdfPageEventHelper() {

            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContent();
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
                float x = document.top(-20);

                if (writer.getCurrentPageNumber() != 1) {
                    //左
                    cb.showTextAligned(PdfContentByte.ALIGN_LEFT, titleStr,
                            document.left(), x, 0);
                }
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

    private static void writeHeadContent(Document document, BaseFont bfChinese, String title, String logoUrl) throws Exception {
        Font font = new Font(bfChinese, 16);
        PdfPTable table = new PdfPTable(3);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.setWidthPercentage(100);
        table.addCell(new Paragraph("", font));
        table.addCell(new Paragraph("", font));
        Image image = Image.getInstance(logoPath(logoUrl));
        table.addCell(image);
        document.add(table);
        Chunk chunk = new Chunk(title, new Font(bfChinese, 26)).setLocalDestination("name");
        Paragraph p = new Paragraph(chunk);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(15f);
        p.setSpacingBefore(15f);
        document.add(new Paragraph("\n"));
        document.add(p);
        document.add(new Paragraph(new Chunk(new LineSeparator())));
        document.add(new Paragraph("\n"));
    }

    private static void writePageContent(PdfWriter writer, List<Question> questions, String title) {
        PdfContentByte cb = writer.getDirectContent();
        PdfOutline root = cb.getRootOutline();

        PdfOutline oline = new PdfOutline(root, PdfAction.gotoLocalPage("name", false), title);
        oline.setOpen(false);
        for (Question question : questions) {
            if (question instanceof InfoQuestion) {
                PdfOutline tempLine = new PdfOutline(oline, PdfAction.gotoLocalPage(question.getId(), false), question.getTitle());
            }
        }
    }

    private static void writeQuestionContent(Document document, BaseFont bfChinese, Question question, Integer type) throws Exception {
        Font font = new Font(bfChinese, 10);
        Font font10 = new Font(bfChinese, 8);
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        //设置不按表格分页
        table.setSplitLate(false);
        PdfPTable table1 = new PdfPTable(1);
        table1.setSplitLate(false);
        table1.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table1.getDefaultCell().setPadding(5f);
        String requiredStr = "";
        if (question.isRequired()) {
            requiredStr += "（必填）";
        }
        table1.addCell(new Paragraph(question.getId() + "、" + question.getTitle() + requiredStr, new Font(bfChinese, 12)));
        if (question.getHint() != null && !"".equals(question.getHint())) {
            table1.addCell(new Paragraph("\t\t提示：" + question.getHint(), font10));
        }
        if (question instanceof TextQuestion || question instanceof TextAreaQuestion || question instanceof IntegerQuestion
                || question instanceof FloatQuestion || question instanceof DatetimeQuestion || question instanceof ScaleQuestion) {
            table1.addCell(new Paragraph("\n"));
            table1.addCell(new Paragraph(new Chunk(new LineSeparator(0.5f, 90, BaseColor.GRAY, Element.ALIGN_LEFT, 0))));
        }
        if (question instanceof SingleSelectQuestion) {
            List<ListItem> questions = ((SingleSelectQuestion) question).getListItems();
            for (ListItem listItem : questions) {
                table1.addCell(new Paragraph(RADIO_BOX + listItem.getId() + "." + listItem.getName(), font));
            }
        }
        if (question instanceof DropdownQuestion) {
            List<ListItem> questions = ((DropdownQuestion) question).getListItems();
            for (ListItem listItem : questions) {
                table1.addCell(new Paragraph(RADIO_BOX + listItem.getId() + "." + listItem.getName(), font));
            }
        }
        if (question instanceof MultiSelectQuestion) {
            List<ListItem> questions = ((MultiSelectQuestion) question).getListItems();
            for (ListItem listItem : questions) {
                table1.addCell(new Paragraph(CHECK_BOX + listItem.getId() + "." + listItem.getName(), font));
            }
        }
        if (question instanceof MatrixQuestion) {
            MatrixQuestion matrixQuestion = (MatrixQuestion) question;
            //取行数
            List<MatrixQuestion.MatrixRow> rows = matrixQuestion.getRows();
            List<MatrixQuestion.MatrixCol> cols = matrixQuestion.getCols();
            if (matrixQuestion.getMatrixType().equals("singledimension")) {
                Question colQuestion = cols.get(0).getQuestion();
                ListQuestion listQuestion = (ListQuestion) colQuestion;
                List<ListItem> listItems = listQuestion.getListItems();
                List<String> optList = new ArrayList<>();
                for (int j = 0, jLength = listItems.size(); j < jLength; j++) {
                    optList.add(listItems.get(j).getName());
                }
                PdfPTable table2 = new PdfPTable(listItems.size() + 1);
                table2.setWidthPercentage(90);
                table2.setSplitLate(false);
                table2.addCell(new Paragraph("", font));
                for (String s : optList) {
                    table2.addCell(new Paragraph(s, font));
                }
                for (int i = 0; i < rows.size(); i++) {
                    table2.addCell(new Paragraph(rows.get(i).getName(), font));
                    if (QuestionType.SingleSelect.equals(colQuestion.getQuestionType())) {
                        for (int j = 0; j < optList.size(); j++) {
                            table2.addCell(new Paragraph(RADIO_BOX, font));
                        }
                    } else {
                        for (int j = 0; j < optList.size(); j++) {
                            table2.addCell(new Paragraph(CHECK_BOX, font));
                        }
                    }
                }
                table1.addCell(table2);
            } else {
                List<String> optList = new ArrayList<>();
                List<Integer> typeList = new ArrayList<>();
                for (int i = 0, length = cols.size(); i < length; i++) {
                    Question colQuestion = cols.get(i).getQuestion();
                    if (QuestionType.SingleSelect.equals(colQuestion.getQuestionType())) {
                        ListQuestion listQuestion = (ListQuestion) colQuestion;
                        List<ListItem> listItems = listQuestion.getListItems();
                        for (int j = 0, jLength = listItems.size(); j < jLength; j++) {
                            optList.add(colQuestion.getTitle() + "-" + listItems.get(j).getName());
                            typeList.add(1);
                        }
                    } else if (QuestionType.MultiSelect.equals(colQuestion.getQuestionType())) {
                        ListQuestion listQuestion = (ListQuestion) colQuestion;
                        List<ListItem> listItems = listQuestion.getListItems();
                        for (int j = 0, jLength = listItems.size(); j < jLength; j++) {
                            optList.add(colQuestion.getTitle() + "-" + listItems.get(j).getName());
                            typeList.add(2);
                        }
                    } else {
                        optList.add(colQuestion.getTitle());
                        typeList.add(3);
                    }
                }
                PdfPTable table2 = new PdfPTable(optList.size() + 1);
                table2.setWidthPercentage(90);
                table2.setSplitLate(false);
                table2.addCell(new Paragraph("", font));
                for (String s : optList) {
                    table2.addCell(new Paragraph(s, font));
                }
                for (int i = 0; i < rows.size(); i++) {
                    table2.addCell(new Paragraph(rows.get(i).getName(), font));
                    for (Integer qtype : typeList) {
                        if (qtype.equals(1)) {
                            table2.addCell(new Paragraph(RADIO_BOX, font));
                        } else if (qtype.equals(2)) {
                            table2.addCell(new Paragraph(CHECK_BOX, font));
                        } else {
                            table2.addCell(new Paragraph("", font));
                        }
                    }
                }
                table1.addCell(table2);
            }
        }
        if (question instanceof AssignmentQuestion) {
            List<ListItem> questions = ((AssignmentQuestion) question).getListItems();
            for (ListItem listItem : questions) {
                table1.addCell(new Paragraph(listItem.getId() + "." + listItem.getName() + " ____________________", font));
            }
        }
        if (question instanceof SortQuestion) {
            List<ListItem> questions = ((SortQuestion) question).getListItems();
            for (ListItem listItem : questions) {
                table1.addCell(new Paragraph(listItem.getId() + "." + listItem.getName() + " ____________________", font));
            }
        }
        if (question instanceof ProvCityQuestion) {
            table1.addCell(new Paragraph("省份：____________________", font));
            table1.addCell(new Paragraph("城市：____________________", font));
            table1.addCell(new Paragraph("区县：____________________", font));
        }
        if (question instanceof CascadeQuestion) {
            List<Map<String, Object>> questions = ((CascadeQuestion) question).getQuestionList();
            for (Map<String, Object> listItem : questions) {
                writeContent(document, listItem.get("id") + "." + listItem.get("title") + " ____________________", font, null);
            }
        }

        if (type == 2) {
            table1.addCell(new Paragraph("\n"));
            if (question.getBeforeStr() != null && !"".equals(question.getBeforeStr())) {
                table1.addCell(new Paragraph("【前置逻辑】\n" + question.getBeforeStr(), font10));
            }
            if (question.getMiddleStr() != null && !"".equals(question.getMiddleStr())) {
                table1.addCell(new Paragraph("【中置逻辑】\n" + question.getMiddleStr(), font10));
            }
            if (question.getAfterStr() != null && !"".equals(question.getAfterStr())) {
                table1.addCell(new Paragraph("【后置逻辑】:\n" + question.getAfterStr(), font10));
            }
            if (question.getValidationStr() != null && !"".equals(question.getValidationStr())) {
                table1.addCell(new Paragraph("【验证逻辑】:\n" + question.getValidationStr(), font10));
            }
        }
        PdfPCell cell = new PdfPCell(table1);
        cell.setPadding(3f);
        //cell.disableBorderSide(15);
        table.addCell(cell);
        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private static void writeQuestionContentNew(Document document, BaseFont bfChinese, Question question, Integer type) throws Exception {
        Font font = new Font(bfChinese, 10);
        font.setStyle(Font.NORMAL);
        Font fontBlur = new Font(bfChinese, 10);
        fontBlur.setColor(new BaseColor(139, 164, 183));
        fontBlur.setStyle(Font.NORMAL);
        Font fontWithRed10 = new Font(bfChinese, 8);
        fontWithRed10.setColor(new BaseColor(255, 67, 81));
        fontWithRed10.setStyle(Font.NORMAL);
        Font fontWithGreen10 = new Font(bfChinese, 8);
        fontWithGreen10.setColor(new BaseColor(39, 177, 88));
        fontWithGreen10.setStyle(Font.NORMAL);
        Font font12 = new Font(bfChinese, 12);
        font12.setStyle(Font.NORMAL);
        Font fontWithRed12 = new Font(bfChinese, 12);
        fontWithRed12.setColor(new BaseColor(255, 67, 81));
        fontWithRed12.setStyle(Font.NORMAL);
        Paragraph pTitle = new Paragraph();
        if (question instanceof InfoQuestion) {
            pTitle.add(new Chunk(question.getId() + "、" + question.getTitle(), new Font(bfChinese, 16)).setLocalDestination(question.getId()));
        } else {
            pTitle.add(new Chunk(question.getId() + "、" + question.getTitle(), font12));
            if (question.isRequired()) {
                pTitle.add(new Chunk("（必填）", fontWithRed12));
            }
            pTitle.setIndentationLeft(10f);
        }
        document.add(pTitle);
        if (question.getHint() != null && !"".equals(question.getHint())) {
            Paragraph pHint = new Paragraph("提示：" + question.getHint(), fontWithGreen10);
            pHint.setIndentationLeft(20f);
            document.add(new Paragraph(pHint));
        }
        if (question instanceof InfoQuestion && !"".equals(question.getControlStr())) {
            Paragraph context = new Paragraph();
            String html = question.getControlStr().replace("<br>", "").replace("<hr>", "").replace("<img>", "").replace("<param>", "")
                    .replace("<link>", "");
            ElementList elementList = XMLWorkerHelper.parseToElementList(html, null);
            for (Element element : elementList) {
                List<Chunk> chunks = element.getChunks();
                for (Chunk chunk : chunks) {
                    chunk.setFont(font);
//                    context.add(chunk);
                }
                context.add(element);
            }
            context.setIndentationLeft(20f);
            document.add(context);
        }
        if (question instanceof TextQuestion || question instanceof TextAreaQuestion || question instanceof IntegerQuestion
                || question instanceof FloatQuestion || question instanceof DatetimeQuestion || question instanceof ScaleQuestion) {
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(new Chunk(new LineSeparator(0.5f, 90, BaseColor.GRAY, Element.ALIGN_LEFT, 0))));
        }
        if (question instanceof SingleSelectQuestion) {
            List<ListItem> questions = ((SingleSelectQuestion) question).getListItems();
            for (ListItem listItem : questions) {
                Chunk gotoStr = null;
                if (listItem.getGotoTarget() != null && !"".equals(listItem.getGotoTarget())) {
                    gotoStr = new Chunk("...选中跳转到" + listItem.getGotoTarget(), fontBlur);
                }
                writeContent(document, RADIO_BOX + listItem.getId() + "." + listItem.getName(), font, gotoStr);
            }
        }
        if (question instanceof DropdownQuestion) {
            List<ListItem> questions = ((DropdownQuestion) question).getListItems();
            for (ListItem listItem : questions) {
                writeContent(document, RADIO_BOX + listItem.getId() + "." + listItem.getName(), font, null);
            }
        }
        if (question instanceof MultiSelectQuestion) {
            List<ListItem> questions = ((MultiSelectQuestion) question).getListItems();
            for (ListItem listItem : questions) {
                Chunk gotoStr = null;
                if (listItem.getGotoTarget() != null && !"".equals(listItem.getGotoTarget())) {
                    gotoStr = new Chunk("...选中跳转到" + listItem.getGotoTarget(), fontBlur);
                }
                writeContent(document, CHECK_BOX + listItem.getId() + "." + listItem.getName(), font, gotoStr);
            }
        }
        if (question instanceof MatrixQuestion) {
            document.add(new Paragraph("\n"));
            MatrixQuestion matrixQuestion = (MatrixQuestion) question;
            //取行数
            List<MatrixQuestion.MatrixRow> rows = matrixQuestion.getRows();
            List<MatrixQuestion.MatrixCol> cols = matrixQuestion.getCols();
            if (matrixQuestion.getMatrixType().equals("singledimension")) {
                Question colQuestion = cols.get(0).getQuestion();
                ListQuestion listQuestion = (ListQuestion) colQuestion;
                List<ListItem> listItems = listQuestion.getListItems();
                List<String> optList = new ArrayList<>();
                for (int j = 0, jLength = listItems.size(); j < jLength; j++) {
                    optList.add(listItems.get(j).getName());
                }
                PdfPTable table2 = new PdfPTable(listItems.size() + 1);
                table2.setWidthPercentage(90);
                table2.setSplitLate(false);
                table2.addCell(new Paragraph("", font));
                for (String s : optList) {
                    PdfPCell cellT = new PdfPCell();
                    Paragraph paragraphT = new Paragraph(s, font);
                    paragraphT.setAlignment(Element.ALIGN_CENTER);
                    cellT.addElement(paragraphT);
                    table2.addCell(cellT);
                }
                for (int i = 0; i < rows.size(); i++) {
                    table2.addCell(new Paragraph(rows.get(i).getName(), font));
                    Paragraph paragraph;
                    if (QuestionType.SingleSelect.equals(colQuestion.getQuestionType())) {
                        for (int j = 0; j < optList.size(); j++) {
                            PdfPCell cell = new PdfPCell();
                            cell.setUseAscender(true);
                            paragraph = new Paragraph(RADIO_BOX, font);
                            paragraph.setAlignment(Element.ALIGN_CENTER);
                            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            cell.addElement(paragraph);
                            table2.addCell(cell);
                        }
                    } else {
                        for (int j = 0; j < optList.size(); j++) {
                            PdfPCell cell = new PdfPCell();
                            cell.setUseAscender(true);
                            paragraph = new Paragraph(CHECK_BOX, font);
                            paragraph.setAlignment(Element.ALIGN_CENTER);
                            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            cell.addElement(paragraph);
                            table2.addCell(cell);
                        }
                    }
                }
                document.add(table2);
            } else {
                List<String> optList = new ArrayList<>();
                List<Integer> typeList = new ArrayList<>();
                for (int i = 0, length = cols.size(); i < length; i++) {
                    Question colQuestion = cols.get(i).getQuestion();
                    if (QuestionType.SingleSelect.equals(colQuestion.getQuestionType())) {
                        ListQuestion listQuestion = (ListQuestion) colQuestion;
                        List<ListItem> listItems = listQuestion.getListItems();
                        for (int j = 0, jLength = listItems.size(); j < jLength; j++) {
                            optList.add(colQuestion.getTitle() + "-" + listItems.get(j).getName());
                            typeList.add(1);
                        }
                    } else if (QuestionType.MultiSelect.equals(colQuestion.getQuestionType())) {
                        ListQuestion listQuestion = (ListQuestion) colQuestion;
                        List<ListItem> listItems = listQuestion.getListItems();
                        for (int j = 0, jLength = listItems.size(); j < jLength; j++) {
                            optList.add(colQuestion.getTitle() + "-" + listItems.get(j).getName());
                            typeList.add(2);
                        }
                    } else {
                        optList.add(colQuestion.getTitle());
                        typeList.add(3);
                    }
                }
                PdfPTable table2 = new PdfPTable(optList.size() + 1);
                table2.setWidthPercentage(90);
                table2.setSplitLate(false);
                table2.addCell(new Paragraph("", font));
                for (String s : optList) {
                    table2.addCell(new Paragraph(s, font));
                }
                for (int i = 0; i < rows.size(); i++) {
                    table2.addCell(new Paragraph(rows.get(i).getName(), font));
                    Paragraph paragraph;
                    for (Integer qtype : typeList) {
                        if (qtype.equals(1)) {
                            PdfPCell cell = new PdfPCell();
                            cell.setUseAscender(true);
                            paragraph = new Paragraph(RADIO_BOX, font);
                            paragraph.setAlignment(Element.ALIGN_CENTER);
                            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            cell.addElement(paragraph);
                            table2.addCell(cell);
                        } else if (qtype.equals(2)) {
                            PdfPCell cell = new PdfPCell();
                            cell.setUseAscender(true);
                            paragraph = new Paragraph(CHECK_BOX, font);
                            paragraph.setAlignment(Element.ALIGN_CENTER);
                            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            cell.addElement(paragraph);
                            table2.addCell(cell);
                        } else {
                            table2.addCell(new Paragraph("", font));
                        }
                    }
                }
                document.add(table2);
            }
        }
        if (question instanceof AssignmentQuestion) {
            List<ListItem> questions = ((AssignmentQuestion) question).getListItems();
            for (ListItem listItem : questions) {
                writeContent(document, listItem.getId() + "." + listItem.getName() + " ____________________", font, null);
            }
        }
        if (question instanceof SortQuestion) {
            List<ListItem> questions = ((SortQuestion) question).getListItems();
            for (ListItem listItem : questions) {
                writeContent(document, listItem.getId() + "." + listItem.getName() + " ____________________", font, null);
            }
        }
        if (question instanceof ProvCityQuestion) {
            writeContent(document, "省份：____________________", font, null);
            writeContent(document, "城市：____________________", font, null);
            writeContent(document, "区县：____________________", font, null);
        }
        if (question instanceof CascadeQuestion) {
            List<Map<String, Object>> questions = ((CascadeQuestion) question).getQuestionList();
            for (Map<String, Object> listItem : questions) {
                writeContent(document, listItem.get("id") + "." + listItem.get("title") + " ____________________", font, null);
            }
        }

        if (type == 2) {
            document.add(new Paragraph("\n"));
            if (question.getBeforeStr() != null && !"".equals(question.getBeforeStr())) {
                document.add(new Paragraph("【前置逻辑】\n" + question.getBeforeStr(), fontWithRed10));
            }
            if (question.getMiddleStr() != null && !"".equals(question.getMiddleStr())) {
                document.add(new Paragraph("【中置逻辑】\n" + question.getMiddleStr(), fontWithRed10));
            }
            if (question.getAfterStr() != null && !"".equals(question.getAfterStr())) {
                document.add(new Paragraph("【后置逻辑】:\n" + question.getAfterStr(), fontWithRed10));
            }
            if (question.getValidationStr() != null && !"".equals(question.getValidationStr())) {
                document.add(new Paragraph("【验证逻辑】:\n" + question.getValidationStr(), fontWithRed10));
            }
        }
        document.add(new Paragraph("\n"));
    }

    private static void writeContent(Document document, String content, Font font, Chunk gotoStr) throws Exception {
        Paragraph p = new Paragraph(content, font);
        if (gotoStr != null) {
            p.add(gotoStr);
        }
        p.setIndentationLeft(20f);
        document.add(new Paragraph(p));
    }

    private static String logoPath(String logoUrl) {
//        String path = new Object() {
//            public String getPath() {
//                return this.getClass().getResource("/").toString();
//            }
//        }.getPath();
        String path = "http://localhost:8073/ringsurveyapi";
        String logoPath;
        if (StringUtils.isNotBlank(logoUrl)) {
            logoPath = path + logoUrl;
        } else {
            logoPath = path + "/resource/platform/img/logo.png";
        }
        return logoPath;
    }
}
