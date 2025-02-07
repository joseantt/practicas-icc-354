package org.example.practica3.entities;


import jakarta.persistence.*;
import lombok.*;

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
}
