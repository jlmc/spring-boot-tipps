package io.costax.food4u.domain.model;

import lombok.Data;

import javax.persistence.*;
import java.io.InputStream;
import java.util.Objects;

@Data
@Entity
@Table(name = "product_photo")
public class Photo {

    @Id
    @Column(name = "product_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(name = "file_name")
    private String fileName;
    private String description;
    @Column(name = "content_type")
    private String contentType;

    private Long size;

    @Column(name = "path")
    private String storedName;

    @Transient
    private InputStream inputStream;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Photo photo = (Photo) o;
        return getId() != null && Objects.equals(getId(), photo.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
