package ee.taltech.iti03022024backend.invjug.repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Table(name = "product")
@Entity
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_generator")
    @SequenceGenerator(name = "product_generator", sequenceName = "product_seq", allocationSize = 1)
    private Long id;

    private String name;
    private Long price;
    private Long quantity;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;
}
