package pl.gren.oze_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gren.oze_app.model.Salesman;

import java.util.List;
import java.util.Optional;

public interface SalesmanRepository extends JpaRepository<Salesman, Long> {

    // poki co tylko name choc jest i name i surname
    Optional<Salesman> findByName(String name);
    Optional<Salesman> findByEmail(String email);
    Optional<Salesman> findById(Long id);
    void deleteById(Long id);
    List<Salesman> findAll();

}