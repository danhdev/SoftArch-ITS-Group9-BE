package com.example.course.service.util.content.strategy;

import org.json.JSONObject;

import java.io.InputStream;

public interface ContentReaderStrategy {
    JSONObject read(InputStream is, String fileName) throws Exception;
}

