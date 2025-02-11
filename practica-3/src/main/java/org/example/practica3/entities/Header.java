package org.example.practica3.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Header implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mockup_id")
    private Mockup mockup;

    @Column(name = "header_key")
    private String key;

    @Column(name = "header_value")
    private String value;
}
