package io.github.jlmc.uploadcsv.domain;

import java.time.LocalDate;
import java.util.Objects;

public enum SpecialDayRepeatsType {
    SINGLE {
        @Override
        public boolean match(LocalDate referenceDate, LocalDate comparingDate) {
            return Objects.equals(referenceDate, comparingDate);
        }
    },
    WEEKLY {
        @Override
        public boolean match(LocalDate referenceDate, LocalDate comparingDate) {
            return referenceDateIsNotAfterTheComparingDate(referenceDate, comparingDate) &&
                    Objects.equals(referenceDate.getDayOfWeek(), comparingDate.getDayOfWeek());
        }
    },
    MONTHLY {
        @Override
        public boolean match(LocalDate referenceDate, LocalDate comparingDate) {
            return referenceDateIsNotAfterTheComparingDate(referenceDate, comparingDate) &&
                    Objects.equals(referenceDate.getDayOfMonth(), comparingDate.getDayOfMonth());
        }
    },
    YEARLY {
        @Override
        public boolean match(LocalDate referenceDate, LocalDate comparingDate) {
            return referenceDateIsNotAfterTheComparingDate(referenceDate, comparingDate) &&
                    Objects.equals(referenceDate.getMonth(), comparingDate.getMonth()) &&
                    Objects.equals(referenceDate.getDayOfMonth(), comparingDate.getDayOfMonth());
        }
    };

    private static boolean referenceDateIsNotAfterTheComparingDate(LocalDate referenceDate, LocalDate comparingDate) {
        return !referenceDate.isAfter(comparingDate);
    }

    public abstract boolean match(LocalDate referenceDate, LocalDate comparingDate);
}
