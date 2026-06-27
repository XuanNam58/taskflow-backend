package org.xuannam.taskflowbackend.common.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractEntity {
    @CreationTimestamp
    Instant createdAt;
    @UpdateTimestamp
    Instant modifiedAt;
}
