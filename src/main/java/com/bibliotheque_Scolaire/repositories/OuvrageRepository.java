package com.bibliotheque_Scolaire.repositories;


import com.bibliotheque_Scolaire.entities.Ouvrage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OuvrageRepository extends JpaRepository<Ouvrage, Long> {

    List<Ouvrage> findByNiveau(String niveau);
    List<Ouvrage> findByDisponible(Boolean disponible);
    List<Ouvrage> findByNiveauAndDisponible(String niveau, Boolean disponible);
}
