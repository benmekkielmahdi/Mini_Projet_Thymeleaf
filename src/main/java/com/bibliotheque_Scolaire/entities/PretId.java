package com.bibliotheque_Scolaire.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PretId implements Serializable {

    private Long eleveId;
    private Long ouvrageId;
    private LocalDate datePret;

    }