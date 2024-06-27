package by.plamya.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@AllArgsConstructor
@Entity
public class PasswordResetToken {

    public PasswordResetToken(User user, String token) {
        this.token = token;
        this.user = user;
    }

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime expiryDate;

    public PasswordResetToken() {
    }

    @PrePersist
    protected void setExpiryDate() {
        this.expiryDate = LocalDateTime.now().plusSeconds(EXPIRATION);
    }
}
