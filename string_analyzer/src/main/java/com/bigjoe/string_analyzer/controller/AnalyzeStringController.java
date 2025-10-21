package com.bigjoe.string_analyzer.controller;

import com.bigjoe.string_analyzer.service.AnalyzeStringServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalyzeStringController {

    private final AnalyzeStringServiceImpl analyzeStringService;

    public AnalyzeStringController(AnalyzeStringServiceImpl analyzeStringService) {
        this.analyzeStringService = analyzeStringService;
    }

    @PostMapping("/strings")
    public ResponseEntity<Map<String, Object>> analyzeString(@RequestBody Map<String, String> request) {
        String input = request.get("value");
        Map<String, Object> result = analyzeStringService.string_analyzer(input);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/strings/{string_value}")
    public ResponseEntity<Map<String, Object>> getSpecificString(@PathVariable("string_value") String string_value) {
        Map<String, Object> result = analyzeStringService.getSpecificString(string_value);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/strings")
    public ResponseEntity<Map<String, Object>> getAllStrings(
            @RequestParam(value = "isPalindrome", required = false) Boolean isPalindrome,
            @RequestParam(value = "minLength", required = false) Integer minLength,
            @RequestParam(value = "maxLength", required = false) Integer maxLength,
            @RequestParam(value = "wordCount", required = false) Integer wordCount,
            @RequestParam(value = "containsCharacter", required = false) String containsCharacter
    ) {
        Map<String, Object> result = analyzeStringService.getAllStrings(isPalindrome, minLength, maxLength, wordCount, containsCharacter);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/strings/filter-by-natural-language")
    public ResponseEntity<Map<String, Object>> filterByNaturalLanguage(@RequestParam("query") String query) {
        Map<String, Object> result = analyzeStringService.filterByNaturalLanguage(query);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/strings/{string_value}")
    public ResponseEntity<Void> deleteRecord(@PathVariable("string_value") String stringValue) {
        analyzeStringService.deleteRecord(stringValue);
        return ResponseEntity.noContent().build();
    }
}
