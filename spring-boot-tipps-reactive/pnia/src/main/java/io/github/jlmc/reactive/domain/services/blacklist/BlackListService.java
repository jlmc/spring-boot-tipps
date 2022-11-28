package io.github.jlmc.reactive.domain.services.blacklist;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BlackListService {

    private final Set<String> blackList = Set.of("true", "false", "TRUE", "FALSE");

    public Collection<String> getForbiddenInputs(Collection<String> inputs) {
        return inputs.stream().filter(it -> Objects.isNull(it) || blackList.contains(it)).collect(Collectors.toSet());
    }

}
