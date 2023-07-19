package com.cryptoshield.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Table(name="DataEncrypted")
@Data
@EntityListeners(AuditingEntityListener.class)
public class DataEncrypted {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long encryptionId;
    private byte[] encryptedData;
    private String encryptionKey;
    @CreatedDate
    private Date createDate;
    @ManyToOne()
    @JoinColumn(name="user_id")
    private User user;
}
