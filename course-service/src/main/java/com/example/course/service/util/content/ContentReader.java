package com.example.course.service.util.content;

import com.example.course.service.util.content.strategy.ContentReaderStrategy;
import com.example.course.service.util.content.strategy.DocxReader;
import com.example.course.service.util.content.strategy.PdfReader;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class ContentReader {

    private final Map<String, ContentReaderStrategy> strategies = new HashMap<>();

    public ContentReader(PdfReader pdfReader, DocxReader docxReader) {
        strategies.put("pdf", pdfReader);
        strategies.put("docx", docxReader);
    }

    public JSONObject readContent(InputStream is, String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        ContentReaderStrategy strategy = strategies.get(ext);
        JSONObject result = new JSONObject();

        if (strategy == null) {
            result.put("error", "Unsupported file type: " + fileName);
            return result;
        }

        try {
            return strategy.read(is, fileName);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            return result;
        }
    }
}
