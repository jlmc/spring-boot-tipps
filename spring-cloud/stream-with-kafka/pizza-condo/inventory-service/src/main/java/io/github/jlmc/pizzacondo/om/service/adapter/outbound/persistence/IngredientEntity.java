package io.github.jlmc.pizzacondo.om.service.adapter.outbound.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Entity
@Table(name = "ingredients")
public class IngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingredients_seq")
    @SequenceGenerator(name = "ingredients_seq", sequenceName = "ingredients_id_seq", allocationSize = 5, initialValue = 1)
    private Long id;
    @NaturalId
    private String name;
    private Integer qty;

    @CreatedDate
    Instant insertedAt;

    @CreatedBy
    String insertedBy;

    @LastModifiedDate
    Instant lastUpdatedAt;

    @LastModifiedBy
    String lastUpdatedBy;

    @Version
    private int version = 0;

    public IngredientEntity() {
    }


}
