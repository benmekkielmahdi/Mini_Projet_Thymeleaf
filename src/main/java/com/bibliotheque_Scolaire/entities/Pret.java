package com.bibliotheque_Scolaire.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
@Table(name = "pret")
public class Pret {

    @EmbeddedId
    private PretId id;

    private LocalDate dateRetourPrevu;

    private LocalDate dateRetour;

    @MapsId("ouvrageId")
    @ManyToOne(fetch = FetchType.EAGER)
    private Ouvrage ouvrage;

    @MapsId("eleveId")
    @ManyToOne(fetch = FetchType.EAGER)
    private Eleve eleve;

    // Helper method to check if the loan is late
    public boolean isLate() {
        if (dateRetour != null) return false; // already returned
        return LocalDate.now().isAfter(dateRetourPrevu);
    }
}