package org.chxei.shmessenger.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@Entity(name = "users")
@DynamicInsert
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "CONSTRAINT_UNIQUE_USERS_USERNAME", columnNames = "username"),
                @UniqueConstraint(name = "CONSTRAINT_UNIQUE_USERS_EMAIL", columnNames = "email"),
                @UniqueConstraint(name = "CONSTRAINT_UNIQUE_USERS_PHONE", columnNames = "phone"),
        }

)
public final class User implements UserDetails, CredentialsContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    //@Column(unique = true)
    private String username;

    @NotEmpty
    private String name;

    @JsonIgnore
    @CreationTimestamp
    private Timestamp creationTimeStamp;

    @JsonIgnore
    @UpdateTimestamp
    private Timestamp updateTimestamp;

    @ManyToOne
    private Country country;

    @Email
    //@Column//(unique = true)
    private String email;

    //@Column(unique = true)
    private String phone;

    private Timestamp birthDate;
    private String pathToProfilePicture;
    private String pathToBackgroundPicture;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private boolean active = true;

    @JsonIgnore
    private boolean isVerified = false;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private Gender gender;

    //todo make separate table for authorities
    @Transient
    private List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("app");

    public User(UserDetails userDetails) {
        this.username = userDetails.getUsername();
        this.password = userDetails.getPassword();
        this.authorities = AuthorityUtils.createAuthorityList("app");
    }

    public User(String username, String name, String email, Timestamp birthDate, String password, Gender gender) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.password = password;
        this.gender = gender;
        this.authorities = AuthorityUtils.createAuthorityList("app");
    }

    public User(String username, boolean active, String password) {
        this.username = username;
        this.password = password;
        this.active = active;
        this.authorities = Stream.of(this.getRole().name())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public User() {
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return active;
    }

    //do not change order, add new values last
    public enum Role {
        USER,
        ADMIN
    }

    @JsonComponent
    public static class UserSerializable extends JsonSerializer<User> {
        @Override
        public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", user.getId());
            jsonGenerator.writeObjectField("username", user.getUsername());
            jsonGenerator.writeObjectField("name", user.getName());
            jsonGenerator.writeObjectField("email", user.getEmail());
            jsonGenerator.writeObjectField("country", user.getCountry() != null ? user.getCountry().getName() : null);
            jsonGenerator.writeObjectField("phone", user.getPhone());
            jsonGenerator.writeObjectField("pathToProfilePicture", user.getPathToProfilePicture());
            jsonGenerator.writeObjectField("pathToBackgroundPicture", user.getPathToBackgroundPicture());
            jsonGenerator.writeObjectField("birthDate", user.getBirthDate().toLocalDateTime());
            jsonGenerator.writeObjectField("active", user.isActive());
            jsonGenerator.writeObjectField("gender", user.getGender() != null ? user.getGender().getName() : null);
            jsonGenerator.writeEndObject();
        }
    }
}
