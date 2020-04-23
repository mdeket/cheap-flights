package com.cheapflights.tickets.domain.model;

import com.cheapflights.tickets.config.security.AuthorityConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "username must not be null.")
    @Size(min = 4, max = 50, message = "username length must be between 4 and 50 characters.")
    private String username;

    @NotNull(message = "firstName must not be null.")
    @Size(min = 1, max = 30, message = "firstName length must be between 1 and 30 characters.")
    private String firstName;

    @NotNull(message = "lastName must not be null.")
    @Size(min = 1, max = 30, message = "lastName length must be between 1 and 30 characters.")
    private String lastName;

    @NotNull(message = "password must not be null.")
    @Size(min = 4, max = 200, message = "lastName length must be longer than 4 character.")
    @JsonIgnore
    private String password;

    // TODO: delete this
    @NotNull
    @Size(min = 1, max = 100)
    @JsonIgnore
    private String salt;

    @NotNull(message = "role must not be null.")
    @Enumerated(EnumType.STRING)
    private AuthorityConstants role;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE, mappedBy = "author")
    @JsonIgnore
    private List<Comment> comments;

    public User(Long id, @NotNull @Size(min = 4, max = 50) String username, @NotNull @Size(min = 1, max = 30) String firstName, @NotNull @Size(min = 1, max = 30) String lastName, @NotNull @Size(min = 4, max = 128) String password, @NotNull @Size(min = 1, max = 100) String salt, @NotNull AuthorityConstants role, List<Comment> comments) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.salt = salt;
        this.role = role;
        this.comments = comments;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(role.toString()));
    }

    public static class UserBuilder {
        private Long id;
        private @NotNull @Size(min = 4, max = 50) String username;
        private @NotNull @Size(min = 1, max = 30) String firstName;
        private @NotNull @Size(min = 1, max = 30) String lastName;
        private @NotNull @Size(min = 4, max = 128) String password;
        private @NotNull @Size(min = 1, max = 100) String salt;
        private @NotNull AuthorityConstants role;
        private List<Comment> comments;

        UserBuilder() {
        }

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder username(@NotNull @Size(min = 4, max = 50) String username) {
            this.username = username;
            return this;
        }

        public UserBuilder firstName(@NotNull @Size(min = 1, max = 30) String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(@NotNull @Size(min = 1, max = 30) String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder password(@NotNull @Size(min = 4, max = 128) String password) {
            this.password = password;
            return this;
        }

        public UserBuilder salt(@NotNull @Size(min = 1, max = 100) String salt) {
            this.salt = salt;
            return this;
        }

        public UserBuilder role(@NotNull AuthorityConstants role) {
            this.role = role;
            return this;
        }

        public UserBuilder comments(List<Comment> comments) {
            this.comments = comments;
            return this;
        }

        public User build() {
            return new User(id, username, firstName, lastName, password, salt, role, comments);
        }

        public String toString() {
            return "User.UserBuilder(id=" + this.id + ", username=" + this.username + ", firstName=" + this.firstName + ", lastName=" + this.lastName + ", password=" + this.password + ", salt=" + this.salt + ", role=" + this.role + ", comments=" + this.comments + ")";
        }
    }
}