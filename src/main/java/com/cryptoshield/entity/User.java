package com.cryptoshield.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;
@Data
@Entity
@Table(name="User", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})})
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    private String name;
    private String username;
    private String email;
    private String password;
    @CreatedDate
    private Date createDate;
    @LastModifiedDate
    private Date updateDate;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    private String otp;

    @Column(name = "verification_token", length = 100)
    private String verificationToken;

    @OneToMany(mappedBy = "user")
    private List<DataEncrypted> userEncryptedList;

}