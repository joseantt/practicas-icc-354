package org.example.practica3.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.practica3.enums.Role;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(indexes = {
        @Index(name = "search_user", columnList = "username")
})
public class UserInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
