package by.plamya.project.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import by.plamya.project.utils.enums.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@AllArgsConstructor
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(length = 3000)
    private String password;

    @Column(nullable = true, updatable = true)
    private String firstname;

    @Column(nullable = true, updatable = true)
    private String lastname;

    @Column(nullable = false, updatable = true)
    private String phone;

    @Column(nullable = true, updatable = true)
    private String address;

    private int active;

    private int emailVerify;


    @Column(updatable = true)
    private String photoLink;


    @ElementCollection(targetClass = ERole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    private Set<ERole> roles = new HashSet<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createdTime;

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    public User() {
    }

    @PrePersist
    protected void onCreated() {
        this.createdTime = LocalDateTime.now();
    }

    /*
     * SECURITY
     */
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

}
