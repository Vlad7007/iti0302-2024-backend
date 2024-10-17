package ee.taltech.iti03022024backend.invjug.repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Table(name = "supplier")
@Entity
public class SupplierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supplier_generator")
    @SequenceGenerator(name = "supplier_generator", sequenceName = "supplier_seq", allocationSize = 1)
    private Long id;
    private String name;
    private String email;
}
