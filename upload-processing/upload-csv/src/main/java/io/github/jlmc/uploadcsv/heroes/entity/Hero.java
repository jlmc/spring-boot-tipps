package io.github.jlmc.uploadcsv.heroes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@NoArgsConstructor
@Data
@Document(collection = "heroes")
public class Hero implements Persistable<String> {
    @Id
    private String id;

    private String name;

    @Field(name = "nick_name")
    private String nickName;

    @CreatedDate
    private Instant createdDate;
    @LastModifiedDate
    private Instant lastModifiedDate;
    @Version
    private Integer version;

    public static Hero createHero(String name, String nickName) {
        return new Hero(name, nickName);
    }

    @JsonIgnore
    @Override
    public boolean isNew() {
        return this.id == null;
    }


    private Hero(String name, String nickName) {
        this.name = name;
        this.nickName = nickName;
    }
}
