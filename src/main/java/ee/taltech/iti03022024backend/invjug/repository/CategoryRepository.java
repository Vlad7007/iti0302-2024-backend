package ee.taltech.iti03022024backend.invjug.repository;

import ee.taltech.iti03022024backend.invjug.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
