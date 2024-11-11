package ee.taltech.iti03022024backend.invjug.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @ManyToMany
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryEntity> categories;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;
}
