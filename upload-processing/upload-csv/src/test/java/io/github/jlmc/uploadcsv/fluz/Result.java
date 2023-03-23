package io.github.jlmc.uploadcsv.fluz;

import java.util.List;

public record Result<T>(
        List<T> items,
        List<String> violations
) {}
