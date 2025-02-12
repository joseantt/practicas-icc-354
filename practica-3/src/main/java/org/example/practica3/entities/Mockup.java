package org.example.practica3.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Mockup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mockup_id")
    private Long id;

    private String name;

    private String description;

    private String path;

    private String accessMethod;

    private int responseCode;

    private String contentType;

    private String body;

    @OneToMany(mappedBy = "mockup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Header> headers;

    private int responseTimeInSecs = 0;

    private LocalDateTime expirationTime;

    private int expirationTimeInHours;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    // TODO: falta agregar JWT token
    private String jwtToken;

    private boolean requiresJwt = false;
}
