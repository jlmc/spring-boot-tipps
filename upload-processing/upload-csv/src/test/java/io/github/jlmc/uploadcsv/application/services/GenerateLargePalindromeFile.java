package io.github.jlmc.uploadcsv.application.services;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateLargePalindromeFile {

    public static void main(String[] args) throws IOException {
        int sizeInMB = 100;
        int targetSizeBytes = sizeInMB * 1024 * 1024;
        String pattern = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        StringBuilder sb = new StringBuilder(targetSizeBytes / 2);
        while (sb.length() < targetSizeBytes / 2) {
            sb.append(pattern);
        }

        String half = sb.toString();
        String fullPalindrome = half + new StringBuilder(half).reverse();
        String palindrome = fullPalindrome.substring(0, targetSizeBytes);

        //String content = "{\"text\":\"" + palindrome + "\"}";
        String content =  palindrome;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("palindrome-100MB.json"))) {
            writer.write(content);
        }

        System.out.println("100MB JSON payload written to 'palindrome-100MB.json'");
    }
}
