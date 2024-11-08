package ee.taltech.iti03022024backend.invjug.repository;

import ee.taltech.iti03022024backend.invjug.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
