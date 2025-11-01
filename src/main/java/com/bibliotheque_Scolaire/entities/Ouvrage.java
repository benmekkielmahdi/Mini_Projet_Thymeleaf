package com.bibliotheque_Scolaire.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Ouvrage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Column(nullable = false)
    private String titre;

    @NotBlank(message = "Le niveau est obligatoire")
    @Column(nullable = false)
    private String niveau;

    private String codeBarres;

    private String imageFileName;
    
    private boolean disponible = true;
    
    @NotNull(message = "La quantité est obligatoire")
    @Column(nullable = false)
    private Integer quantite = 1; // Quantité disponible en stock

}



