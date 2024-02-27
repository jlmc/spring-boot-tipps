package io.github.jlmc.cwr.service.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Embeddable
public class AuditingData {

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "modified_at", nullable = false)
    @LastModifiedDate
    private Instant modifiedAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    @CreatedBy
    private String createdBy;

    @Column(name = "modified_by", nullable = false)
    @LastModifiedBy
    private String modifiedBy;

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
