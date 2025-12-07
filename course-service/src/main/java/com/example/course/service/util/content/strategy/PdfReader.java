package com.example.course.service.util.content.strategy;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class PdfReader implements ContentReaderStrategy {
    @Override
    public JSONObject read(InputStream is, String fileName) throws Exception {
        JSONObject result = new JSONObject();
        try  {
            byte[] pdfBytes = is.readAllBytes();
            PDDocument document = Loader.loadPDF(pdfBytes);

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            result.put("fileName", fileName);
            result.put("pages", document.getNumberOfPages());
            result.put("content", text);

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }
}

