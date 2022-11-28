package io.github.jlmc.reactive.more;

import java.util.Collection;
import java.util.stream.Collectors;

public class Dto {

    private String id;
    private String name;
    private Collection<Sa> sas;

    public static class Sa {
        private  String sum;

        public Sa(String sum) {
            this.sum = sum;
        }
    }

    static Dto from (Person person, Collection<Account> accounts) {

        final Dto dto = new Dto();
        dto.id = person.id();
        dto.name = person.name();

        dto.sas =
                accounts.stream()
                        .map(a -> new Sa(a.id() + " --- " + a.number()))
                        .collect(Collectors.toList());

        return dto;
    }
}
