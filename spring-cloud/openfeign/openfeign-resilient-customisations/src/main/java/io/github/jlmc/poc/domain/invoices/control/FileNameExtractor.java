package io.github.jlmc.poc.domain.invoices.control;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FileNameExtractor {
    private static final String PATTERN = "^(.*?)(\\.[^.]*$|$)";
    private static final Pattern REGEX = Pattern.compile(PATTERN);

    public String extract(String fileName) {
        Matcher matcher = REGEX.matcher(fileName);

        boolean exists = matcher.find();
        if (exists) {
            return matcher.group(1);
        } else {
            return fileName;
        }
    }
}
