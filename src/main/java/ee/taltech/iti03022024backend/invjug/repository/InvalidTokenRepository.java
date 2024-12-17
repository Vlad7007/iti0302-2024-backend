package ee.taltech.iti03022024backend.invjug.repository;

import ee.taltech.iti03022024backend.invjug.entities.InvalidTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidTokenEntity, Long> {
    boolean existsByToken(String token);
}