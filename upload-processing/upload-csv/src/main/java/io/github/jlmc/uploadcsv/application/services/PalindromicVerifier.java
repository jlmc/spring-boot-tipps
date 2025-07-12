package io.github.jlmc.uploadcsv.application.services;

import org.springframework.stereotype.Component;

@Component
public class PalindromicVerifier {

    public boolean isPalindrome(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        int left = 0;
        int right = text.length() - 1;

        while (left < right) {
            char leftChar = text.charAt(left);
            char rightChar = text.charAt(right);

            if (!Character.isLetterOrDigit(leftChar)) {
                left++;
                continue;
            }

            if (!Character.isLetterOrDigit(rightChar)) {
                right--;
                continue;
            }

            if (Character.toLowerCase(leftChar) != Character.toLowerCase(rightChar)) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }
}
