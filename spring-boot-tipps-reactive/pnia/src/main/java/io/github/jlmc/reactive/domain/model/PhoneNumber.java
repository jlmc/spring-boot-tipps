package io.github.jlmc.reactive.domain.model;

import lombok.Getter;
import lombok.ToString;

import java.util.Optional;
import java.util.regex.Pattern;

@Getter
@ToString
public class PhoneNumber {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^(\\s+)?(\\+|00)?((\\d(\\s+)?){7,12}|(\\d(\\s+)?){3})$");

    private final String originalTxt;
    private final String number;
    private Sector sector;
    private String prefix;

    private PhoneNumber(String number, String originalTxt) {
        this.number = number;
        this.originalTxt = originalTxt;
    }

    public static PhoneNumber of(String txt) {
        if (!isValidPhoneNumber(txt)) {
            throw new IllegalArgumentException("The phone Number <" + txt + "> is nor valid");
        }

        String cleanPhoneNumber =
                txt.replaceAll("\\s", "")
                   .replaceFirst("^\\+|00", "");

        return new PhoneNumber(cleanPhoneNumber, txt);
    }

    /**
     * A number is considered valid if:
     * <pre>
     * - it contains only digits
     * - an optional leading +" +
     * - whitespace anywhere except immediately after the +
     * - contains exactly 3 digits or more than 6 and less than 13
     * - 00 is acceptable as replacement for the leading +
     * </pre>
     *
     * @param txt the phone number that should be validated.
     * @return true if the phoneNumber is valid, false otherwise. If the phoneNumber is null then it will return false.
     */
    public static boolean isValidPhoneNumber(String txt) {
        return
        Optional.ofNullable(txt)
                //.filter(Objects::nonNull)
                //.map(txt -> txt.replaceAll("\\s+", ""))
                .map(it -> it.startsWith("00") ? it.replaceFirst("00", "+") : it)
                .map(it -> PHONE_NUMBER_PATTERN.matcher(it).matches())
                .orElse(false);
    }

    public PhoneNumber withSector(Sector sector) {
        var copy = this.copy();
        copy.sector = sector;
        return copy;
    }

    public PhoneNumber withPrefix(String prefix) {
        var copy = this.copy();
        copy.prefix = prefix;
        return copy;
    }

    private PhoneNumber copy() {
        var copy = new PhoneNumber(this.number, this.originalTxt);
        copy.sector = this.sector;
        copy.prefix = this.prefix;

        return copy;
    }
}
