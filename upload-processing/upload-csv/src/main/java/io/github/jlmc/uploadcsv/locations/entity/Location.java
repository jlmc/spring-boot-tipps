package io.github.jlmc.uploadcsv.locations.entity;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvRecurse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Document(collation = "locations")


@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Location {
    @CsvBindByName
    @EqualsAndHashCode.Include
    @ToString.Include

    @Id
    private String id;
    @ToString.Include
    @Indexed(name = "accountId_1", background = true)
    private String accountId;
    @ToString.Include
    private String name;
    private String imageUrl;
    private String phoneNumber;
    private ZoneId timeZone;
    private Address address;
    private Map<DayOfWeek, Set<Slot>> openHours;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @Version
    private Integer version;


    public Location update(Location that) {
        this.name = that.name;
        this.imageUrl = that.imageUrl;
        this.phoneNumber = that.phoneNumber;
        this.timeZone = that.timeZone;
        this.address = that.address;
        this.openHours = that.openHours;
        return this;
    }
}
