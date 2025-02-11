package org.example.practica3.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
public class Project {

    @Id
    @GeneratedValue
    private long id;
    private String name;

    @ManyToOne
    //@JoinColumn(name = "id")
    private UserInfo userInfo;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mockup> mockups;
}
