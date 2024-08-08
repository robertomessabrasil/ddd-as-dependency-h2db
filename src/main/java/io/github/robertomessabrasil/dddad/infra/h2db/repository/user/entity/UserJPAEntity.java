package io.github.robertomessabrasil.dddad.infra.h2db.repository.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_user")
public class UserJPAEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "role")
    private int role;

    public UserJPAEntity() {
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

    public int getId() {
        return id;
    }

    public UserJPAEntity setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserJPAEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserJPAEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public int getRole() {
        return role;
    }

    public UserJPAEntity setRole(int role) {
        this.role = role;
        return this;
    }
}
