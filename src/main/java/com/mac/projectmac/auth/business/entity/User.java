package com.mac.projectmac.auth.domain.entity;

import com.mac.projectmac.auth.domain.exception.AuthErrorCode;
import com.mac.projectmac.global.domain.common.error.exception.ValidationException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public User(String loginId, String email, String password,
                String name, String nickname, UserRole role,
                UserStatus status, SocialType socialType) {
        this.loginId    = loginId;
        this.email      = email;
        this.password   = password;
        this.name       = name;
        this.nickname   = nickname;
        this.role       = role       != null ? role       : UserRole.USER;
        this.status     = status     != null ? status     : UserStatus.ACTIVE;
        this.socialType = socialType != null ? socialType : SocialType.EMAIL;
        this.isDeleted  = false;
    }

    public void ban() {
        if (this.status == UserStatus.BANNED) {
            throw new ValidationException(AuthErrorCode.USER_ALREADY_BANNED);
        }
        this.status = UserStatus.BANNED;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public boolean isBanned() {
        return this.status == UserStatus.BANNED;
    }

    public enum UserRole   { USER, ADMIN }
    public enum UserStatus { ACTIVE, BANNED }
    public enum SocialType { EMAIL, KAKAO, GOOGLE }
}
