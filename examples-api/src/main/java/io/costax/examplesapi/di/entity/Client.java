package io.costax.examplesapi.di.entity;

import java.util.Objects;

public class Client {
    private Long id;
    private String name;
    private String email;
    private boolean enable = false;

    private Client(final Long id, final String name, final String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static Client of(final Long id, final String name, final String email) {
        return new Client(id, name, email);
    }

    public Long getId() {
        return id;
    }

    public void enable() {
        this.enable = true;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEnable() {
        return enable;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        final Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", enable=" + enable +
                '}';
    }
}