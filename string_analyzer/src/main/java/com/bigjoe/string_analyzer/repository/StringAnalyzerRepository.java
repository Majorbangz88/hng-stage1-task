package com.bigjoe.string_analyzer.repository;

import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class StringAnalyzerRepository {

    private final Map<String, Map<String, Object>> storage = new LinkedHashMap<>();
}
