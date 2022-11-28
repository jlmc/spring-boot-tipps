package io.github.jlmc.pniakotlin.domain.model

import java.util.regex.Pattern

data class PhoneNumber(
    val originalTxt: String,
    val number: String,
    val sector: Sector? = null,
    val prefix: String? = null,

    ) {

    fun withSector(sector: Sector?): PhoneNumber = copy(sector = sector)

    fun withPrefix(prefix: String?): PhoneNumber = copy(prefix = prefix)

    companion object {
        private val PHONE_NUMBER_PATTERN = Pattern.compile("^(\\s+)?(\\+|00)?((\\d(\\s+)?){7,12}|(\\d(\\s+)?){3})$")

        /**
         * A number is considered valid if:
         * <pre>
         * - it contains only digits
         * - an optional leading +" +
         * - whitespace anywhere except immediately after the +
         * - contains exactly 3 digits or more than 6 and less than 13
         * - 00 is acceptable as replacement for the leading +
        </pre> *
         *
         * @param txt the phone number that should be validated.
         * @return true if the phoneNumber is valid, false otherwise. If the phoneNumber is null then it will return false.
         */
        fun isValidPhoneNumber(txt: String?): Boolean {
            val result =
                txt?.let {
                    if (it.startsWith("00"))
                        it.replaceFirst("00".toRegex(), "+")
                    else it
                }?.let {
                    PHONE_NUMBER_PATTERN.matcher(it).matches()
                } ?: false

            return result
        }

        fun of(txt: String): PhoneNumber {
            require(isValidPhoneNumber(txt)) { "The phone Number <$txt> is nor valid" }

            val cleanPhoneNumber =
                txt.replace("\\s".toRegex(), "")
                    .replaceFirst("^\\+|00".toRegex(), "")

            return PhoneNumber(originalTxt = txt, number = cleanPhoneNumber)
        }
    }
}
