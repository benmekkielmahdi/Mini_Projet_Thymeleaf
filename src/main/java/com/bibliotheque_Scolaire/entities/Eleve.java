package com.bibliotheque_Scolaire.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Eleve {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "La classe est obligatoire")
    @Column(nullable = false)
    private String classe;

    @NotBlank(message = "Le matricule est obligatoire")
    @Column(nullable = false, unique = true)
    private String matricule;

}