package fr.inventory.packaging.repository;

import fr.inventory.packaging.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Unit} entities.
 **/
public interface UnitRepository extends JpaRepository<Unit,String> {
}
