package ee.taltech.iti03022024backend.invjug.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@Entity
@Table(name = "invalid_tokens")
public class InvalidTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String token;

    @Column(name = "invalidated_at", nullable = false)
    private Date invalidatedAt;
}
