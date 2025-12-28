package com.englishsupporter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "mywords",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_mywords_word_id",
        columnNames = "word_id"
    ),
    indexes = {
        @Index(name = "idx_mywords_word_id", columnList = "word_id"),
        @Index(name = "idx_mywords_last_shown", columnList = "last_shown")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyWord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;
    
    @Column(name = "last_shown")
    private LocalDateTime lastShown;
    
    @Column(name = "show_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer showCount = 0;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}


