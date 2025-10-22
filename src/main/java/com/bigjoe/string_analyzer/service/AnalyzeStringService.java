package com.bigjoe.string_analyzer.service;

import java.util.HashMap;
import java.util.Map;

public interface AnalyzeStringService {

    Map<String, Object> string_analyzer(String input);

    boolean isPalindrome(String input);

    int uniqueCharactersCount(String input);

    int getWordCount(String input);

    String getSHA256Hash(String input);

    HashMap<Character, Integer> getFrequencyMap(String input);
}
