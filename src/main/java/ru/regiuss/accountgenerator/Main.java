package ru.regiuss.accountgenerator;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) throws Exception {;
        Settings settings = new Settings();
        settings.load();

        XWPFDocument result = new XWPFDocument(OPCPackage.open("./template.docx"));
        PrintWriter writer = new PrintWriter(new FileWriter("result.txt"));
        result.getDocument().unsetBody();
        CTDocument1 result1 = result.getDocument();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int size = settings.getSymbols().length;
        for (int i = 0; i < settings.getCount(); i++) {
            String login = settings.getLogin().replace("%number%", Integer.toString(i+1));
            StringBuilder pass = new StringBuilder();
            for (int j = 0; j < settings.getLength(); j++) {
                pass.append(settings.getSymbols()[rnd.nextInt(size)]);
            }
            Map<String, String> replace = Map.of(
                    "$$login$$", login,
                    "$$pass$$", pass.toString()
            );
            writer.println(String.format("%s %s", login, pass));
            System.out.println(replace);
            XWPFDocument doc = docReplace(replace);
            if(i < settings.getCount()-1)doc.createParagraph().createRun().addBreak(BreakType.PAGE);
            CTDocument1 doc1 = doc.getDocument();
            if(doc1 != null){
                CTBody body = doc1.getBody();
                if(body != null){
                    CTBody mergedCTBody = result1.addNewBody();
                    mergedCTBody.set(body);
                }
            }
            doc.close();
        }
        result.write(new FileOutputStream("./result.docx"));
        writer.close();
    }

    public static XWPFDocument docReplace(Map<String, String> replase) throws Exception {
        XWPFDocument doc = new XWPFDocument(OPCPackage.open("./template.docx"));
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null) {
                        for(Map.Entry<String, String> e : replase.entrySet()){
                            if(text.contains(e.getKey()))text = text.replace(e.getKey(), e.getValue());
                        }
                        r.setText(text, 0);
                    }
                }
            }
        }

        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null) {
                                for(Map.Entry<String, String> e : replase.entrySet()){
                                    if(text.contains(e.getKey()))text = text.replace(e.getKey(), e.getValue());
                                }
                                r.setText(text, 0);
                            }
                        }
                    }
                }
            }
        }
        return doc;
    }
}
