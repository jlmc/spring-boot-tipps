package io.github.jlmc.uploadcsv.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

@Component
public class PalindromicVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(PalindromicVerifier.class);

    public boolean isPalindrome(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        int left = 0;
        int right = text.length() - 1;

        while (left < right) {
            char leftChar = text.charAt(left);
            char rightChar = text.charAt(right);

            LOGGER.debug("Checking if [{}]left {} and [{}]right {} are equals", left, leftChar, right, rightChar);

            if (!Character.isLetterOrDigit(leftChar)) {
                left++;
                continue;
            }

            if (!Character.isLetterOrDigit(rightChar)) {
                right--;
                continue;
            }

            if (Character.toLowerCase(leftChar) != Character.toLowerCase(rightChar)) {
                LOGGER.debug("Palindromic verifier is NOT OK");
                return false;
            }

            left++;
            right--;
        }

        LOGGER.debug("Palindromic verifier is OK");
        return true;
    }

    public boolean isPalindromeFile(Path path) throws IOException {
        LOGGER.debug("Palindromic verifying the file {}", path);
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
            long left = 0;
            long right = raf.length() - 1;

            while (left < right) {
                // Read left char
                raf.seek(left);
                int cLeft = raf.read();
                if (!Character.isLetterOrDigit(cLeft)) {
                    left++;
                    continue;
                }

                // Read right char
                raf.seek(right);
                int cRight = raf.read();
                if (!Character.isLetterOrDigit(cRight)) {
                    right--;
                    continue;
                }

                LOGGER.debug("Checking if [{}]left {} and [{}]right {} are equals", left, cLeft, right, cRight);

                if (Character.toLowerCase(cLeft) != Character.toLowerCase(cRight)) {
                    LOGGER.debug("Palindromic verifier is NOT OK");
                    return false;
                }
                left++;
                right--;
            }

            LOGGER.debug("Palindromic verifier is OK");

            return true;
        }
    }

    public boolean isPalindromeHighPerformanceFile(Path path) throws IOException {
        LOGGER.debug("Palindromic verifying (memory-mapped) file {}", path);

        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r");
             FileChannel channel = raf.getChannel()) {

            long fileSize = channel.size();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);

            int left = 0;
            int right = (int) fileSize - 1;

            byte cLeft, cRight;

            while (left < right) {
                do {
                    cLeft = buffer.get(left++);
                } while (left < right && !Character.isLetterOrDigit(cLeft));

                do {
                    cRight = buffer.get(right--);
                } while (left < right && !Character.isLetterOrDigit(cRight));

                if (Character.toLowerCase(cLeft) != Character.toLowerCase(cRight)) {
                    return false;
                }
            }

            return true;
        }
    }
}
