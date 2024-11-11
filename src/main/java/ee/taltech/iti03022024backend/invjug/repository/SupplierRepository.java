package ee.taltech.iti03022024backend.invjug.repository;

import ee.taltech.iti03022024backend.invjug.entities.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, Long> {
}
