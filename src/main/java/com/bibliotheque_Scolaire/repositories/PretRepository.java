package com.bibliotheque_Scolaire.repositories;


import com.bibliotheque_Scolaire.entities.Pret;
import com.bibliotheque_Scolaire.entities.PretId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PretRepository extends JpaRepository<Pret, PretId> {

    // Find all active loans (not returned yet)
    List<Pret> findByDateRetourIsNull();

    // Find all loans by student
    List<Pret> findByEleveId(Long eleveId);

    // Find all loans by book
    List<Pret> findByOuvrageId(Long ouvrageId);

    // Find late loans
    @Query("SELECT p FROM Pret p WHERE p.dateRetour IS NULL AND p.dateRetourPrevu < :today")
    List<Pret> findLateLoans(@Param("today") LocalDate today);

    // Find loans by class
    @Query("SELECT p FROM Pret p WHERE p.eleve.classe = :classe")
    List<Pret> findByClasse(@Param("classe") String classe);

    // Find loans by niveau
    @Query("SELECT p FROM Pret p WHERE p.ouvrage.niveau = :niveau")
    List<Pret> findByNiveau(@Param("niveau") String niveau);

    // Find loans by code barres
    @Query("SELECT p FROM Pret p WHERE p.ouvrage.codeBarres = :codeBarres")
    List<Pret> findByOuvrageCodeBarres(@Param("codeBarres") String codeBarres);

    // Find loans by class and late status
    @Query("SELECT p FROM Pret p WHERE p.eleve.classe = :classe AND p.dateRetour IS NULL AND p.dateRetourPrevu < :today")
    List<Pret> findLateLoansByClasse(@Param("classe") String classe, @Param("today") LocalDate today);

    // Statistics: loans by niveau
    @Query("SELECT p.ouvrage.niveau, COUNT(p) FROM Pret p GROUP BY p.ouvrage.niveau")
    List<Object[]> countLoansByNiveau();

    // Statistics: late loans per month
    @Query("SELECT FUNCTION('YEAR', p.dateRetourPrevu), FUNCTION('MONTH', p.dateRetourPrevu), COUNT(p) " +
           "FROM Pret p WHERE p.dateRetour IS NULL AND p.dateRetourPrevu < :today " +
           "GROUP BY FUNCTION('YEAR', p.dateRetourPrevu), FUNCTION('MONTH', p.dateRetourPrevu)")
    List<Object[]> countLateLoansByMonth(@Param("today") LocalDate today);
}
