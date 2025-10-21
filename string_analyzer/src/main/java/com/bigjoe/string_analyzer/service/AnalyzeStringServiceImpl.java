package com.bigjoe.string_analyzer.service;

import com.bigjoe.string_analyzer.exception.InvalidDataTypeException;
import com.bigjoe.string_analyzer.exception.InvalidInputException;
import com.bigjoe.string_analyzer.exception.StringNotFoundException;
import com.bigjoe.string_analyzer.exception.StringAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class AnalyzeStringServiceImpl implements AnalyzeStringService {



    public Map<String, Object> string_analyzer(String input) {
        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> properties = new HashMap<>();

        validateInput(input, response);

        int length = input.length();
        boolean is_palindrome = isPalindrome(input);
        int unique_characters = uniqueCharactersCount(input);
        int word_count = getWordCount(input);
        String sha256_hash = getSHA256Hash(input);
        HashMap<Character, Integer> character_frequency_map = getFrequencyMap(input);

        properties.put("length", length);
        properties.put("is_palindrome", is_palindrome);
        properties.put("unique_characters", unique_characters);
        properties.put("word_count", word_count);
        properties.put("sha256_hash", sha256_hash);
        properties.put("character_frequency_map", character_frequency_map);

        response.put("id", sha256_hash);
        response.put("value", input);
        response.put("properties", properties);
        response.put("created_at", java.time.Instant.now().toString());

        return response;
    }

    private void validateInput(String input, Map<String, Object> response) {
        if (response.containsKey(input)) {
            throw new StringAlreadyExistsException("String already exists in the system");
        }
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputException("Invalid request body or missing \"value\" field");
        }
        if (!input.matches("^[\\p{Print}\\s]+$")) { // Allows printable characters
            throw new InvalidDataTypeException("Invalid data type for \"value\" (must be string)");
        }
    }

    public boolean isPalindrome(String input) {
        String stringEntered = input.toLowerCase();
        String reversed = new StringBuilder(stringEntered).reverse().toString();

        return stringEntered.equals(reversed);
    }

    public int uniqueCharactersCount(String input) {
        String distinct = "";
        for (int index = 0; index < input.length(); index++) {
            char current = input.charAt(index);

            if (distinct.indexOf(current) == -1) {
                distinct += current;
            }
        }
        return distinct.length();
    }

    public int getWordCount(String input) {
        if (input == null || input.trim().isEmpty()) return 0;
        return input.trim().split("\\s+").length;
    }

    public String getSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public HashMap<Character, Integer> getFrequencyMap(String input) {
        HashMap<Character, Integer> content = new HashMap<>();

        for (int index = 0; index < input.length(); index++) {
            char character = input.charAt(index);

            content.put(character, content.getOrDefault(character, 0) + 1);
        }

        return content;
    }

    public Map<String, Object> getSpecificString(String input) {
        if (!response.containsKey(input)) {
            throw new StringNotFoundException("String does not exist in the system");
        }
        Object record = response.get(input);

        if (!(record instanceof Map)) {
            throw new IllegalStateException("Stored data format is invalid");
        }

        return (Map<String, Object>) record;
    }

    public Map<String, Object> getAllStrings(Boolean isPalindrome, Integer minLength, Integer maxLength,
                                             Integer wordCount, String containsCharacter) {

        validateQueryInputs(isPalindrome, minLength, maxLength, wordCount, containsCharacter);

        List<Map<String, Object>> filteredResults = new ArrayList<>();

        for (Object record : response.values()) {
            if (!(record instanceof Map)) continue;

            Map<String, Object> stringData = (Map<String, Object>) record;
            Map<String, Object> properties = (Map<String, Object>) stringData.get("properties");

            String value = (String) stringData.get("value");
            int length = (int) properties.get("length");
            boolean palindrome = (boolean) properties.get("is_palindrome");
            int words = (int) properties.get("word_count");

            if (isPalindrome != null && palindrome != isPalindrome) continue;
            if (minLength != null && length < minLength) continue;
            if (maxLength != null && length > maxLength) continue;
            if (wordCount != null && words != wordCount) continue;
            if (containsCharacter != null && !value.toLowerCase().contains(containsCharacter.toLowerCase())) continue;

            filteredResults.add(stringData);
        }

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("data", filteredResults);
        responseBody.put("count", filteredResults.size());

        Map<String, Object> filtersApplied = new LinkedHashMap<>();
        if (isPalindrome != null) filtersApplied.put("is_palindrome", isPalindrome);
        if (minLength != null) filtersApplied.put("min_length", minLength);
        if (maxLength != null) filtersApplied.put("max_length", maxLength);
        if (wordCount != null) filtersApplied.put("word_count", wordCount);
        if (containsCharacter != null) filtersApplied.put("contains_character", containsCharacter);

        responseBody.put("filters_applied", filtersApplied);

        return responseBody;
    }

    private void validateQueryInputs(Boolean isPalindrome, Integer minLength, Integer maxLength,
                                     Integer wordCount, String containsCharacter) {
        if (isPalindrome || minLength != null && minLength < 0 || maxLength != null && maxLength < 0 ||
                minLength != null && maxLength != null && minLength > maxLength || wordCount != null && wordCount < 0 ||
                containsCharacter != null && containsCharacter.length() != 1) {
            throw new InvalidInputException("Invalid query parameter values or types");
        }
    }

    public Map<String, Object> filterByNaturalLanguage(String query) {
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> parsedFilters = new LinkedHashMap<>();

        if (query == null || query.trim().isEmpty()) {
            throw new InvalidInputException("400 Bad Request: Missing or empty query");
        }

        String normalized = query.toLowerCase().trim();

        try {
            if (normalized.contains("palindromic")) {
                parsedFilters.put("is_palindrome", true);
            }

            if (normalized.contains("single word")) {
                parsedFilters.put("word_count", 1);
            }

            if (normalized.matches(".*longer than (\\d+) characters.*")) {
                int minLength = Integer.parseInt(normalized.replaceAll(".*longer than (\\d+) characters.*", "$1")) + 1;
                parsedFilters.put("min_length", minLength);
            }

            if (normalized.matches(".*letter ([a-zA-Z]).*")) {
                String letter = normalized.replaceAll(".*letter ([a-zA-Z]).*", "$1").toLowerCase();
                parsedFilters.put("contains_character", letter);
            }

            if (normalized.contains("first vowel")) {
                parsedFilters.put("is_palindrome", true);
                parsedFilters.put("contains_character", "a");
            }

            if (parsedFilters.isEmpty()) {
                throw new InvalidInputException("Unable to parse natural language query");
            }

            List<Map<String, Object>> filteredData = new ArrayList<>();

            for (Map.Entry<String, Object> entry : this.response.entrySet()) {
                Map<String, Object> item = (Map<String, Object>) entry.getValue();
                Map<String, Object> properties = (Map<String, Object>) item.get("properties");

                boolean matches = true;

                for (Map.Entry<String, Object> filter : parsedFilters.entrySet()) {
                    String key = filter.getKey();
                    Object value = filter.getValue();

                    if (key.equals("is_palindrome") && !Objects.equals(properties.get("is_palindrome"), value)) {
                        matches = false;
                    } else if (key.equals("word_count") && !Objects.equals(properties.get("word_count"), value)) {
                        matches = false;
                    } else if (key.equals("min_length") && ((int) properties.get("length") < (int) value)) {
                        matches = false;
                    } else if (key.equals("contains_character")) {
                        String charToFind = (String) value;
                        String actualValue = (String) item.get("value");
                        if (!actualValue.toLowerCase().contains(charToFind)) {
                            matches = false;
                        }
                    }
                }

                if (matches) filteredData.add(item);
            }

            if (filteredData.isEmpty()) {
                throw new IllegalArgumentException("422 Unprocessable Entity: Query parsed but resulted in no matching data");
            }

            response.put("data", filteredData);
            response.put("count", filteredData.size());

            Map<String, Object> interpreted = new LinkedHashMap<>();
            interpreted.put("original", query);
            interpreted.put("parsed_filters", parsedFilters);

            response.put("interpreted_query", interpreted);
            return response;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("400 Bad Request: Unable to parse natural language query");
        }
    }


}
