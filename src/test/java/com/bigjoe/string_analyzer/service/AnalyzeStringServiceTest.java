package com.bigjoe.string_analyzer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class AnalyzeStringServiceTest {

    @Autowired
    AnalyzeStringService service;

    @Test
    public void testThatInputIsPalindrome() {
        String input = "Ekitike";
        boolean isPalindrome = service.isPalindrome(input);
        Assertions.assertTrue(isPalindrome);
    }

    @Test
    public void testThatInputIsNotPalindrome() {
        String input = "Hello";
        boolean isPalindrome = service.isPalindrome(input);
        Assertions.assertFalse(isPalindrome);
    }

    @Test
    public void testUniqueCharacterCounts() {
        String input = "Viktor Gyokeres is such a fantastic player";
        int count = service.uniqueCharactersCount(input);
        int expectedUniqueChars = 19;
        Assertions.assertEquals(expectedUniqueChars, count);
    }

    @Test
    public void getWordCount() {
        String input = "Viktor Gyokeres is such a fantastic player";
        int count = 7;
        Assertions.assertEquals(count, service.getWordCount(input));
    }

    @Test
    public void getSHA256Hash() {
        String input = "Hello";
        String ssh256Hash = "185f8db32271fe25f561a6fc938b2e264306ec304eda518007d1764826381969";
        String result = service.getSHA256Hash(input);

        Assertions.assertEquals(ssh256Hash, result);
    }

    @Test
    public void getFrequencyMap() {
        String input = "Hello";
        Map<Character, Integer> frequency = new HashMap<>();
        frequency.put('H', 1);
        frequency.put('e', 1);
        frequency.put('l', 2);
        frequency.put('o', 1);

        Map<Character, Integer> actual = service.getFrequencyMap(input);
        Assertions.assertEquals(frequency, actual);
    }
}
