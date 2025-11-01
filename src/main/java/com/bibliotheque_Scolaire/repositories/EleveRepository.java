package com.bibliotheque_Scolaire.repositories;

import com.bibliotheque_Scolaire.entities.Eleve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EleveRepository extends JpaRepository<Eleve, Long> {
    
    List<Eleve> findByClasse(String classe);
    
    Eleve findFirstByMatricule(String matricule);
    
    boolean existsByMatricule(String matricule);
    
    @Query("SELECT COUNT(e) > 0 FROM Eleve e WHERE e.matricule = :matricule AND e.id != :id")
    boolean existsByMatriculeAndIdNot(@Param("matricule") String matricule, @Param("id") Long id);
}
