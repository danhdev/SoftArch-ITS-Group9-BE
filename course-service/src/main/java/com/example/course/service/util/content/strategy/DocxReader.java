package com.example.course.service.util.content.strategy;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class DocxReader implements ContentReaderStrategy {
    @Override
    public JSONObject read(InputStream is, String fileName) throws Exception {
        JSONObject result = new JSONObject();

        try {
            String text;

            if (fileName.toLowerCase().endsWith(".docx")) {
                try (XWPFDocument docx = new XWPFDocument(is)) {
                    StringBuilder sb = new StringBuilder();
                    List<XWPFParagraph> paragraphs = docx.getParagraphs();
                    for (XWPFParagraph p : paragraphs) {
                        sb.append(p.getText()).append("\n");
                    }
                    text = sb.toString();
                }
            } else {
                throw new IllegalArgumentException("Unsupported file format for Word: " + fileName);
            }

            result.put("fileName", fileName);
            result.put("content", text);

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }
}

