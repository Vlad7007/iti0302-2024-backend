package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.controller.Product;
import ee.taltech.iti03022024backend.invjug.mapping.ProductMapper;
import ee.taltech.iti03022024backend.invjug.repository.ProductEntity;
import ee.taltech.iti03022024backend.invjug.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public ProductService(ProductMapper productMapper, ProductRepository productRepository) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        ProductEntity productEntity = productMapper.toProductEntity(product);
        ProductEntity savedEntity = productRepository.save(productEntity);

        return productMapper.toProduct(savedEntity);
    }

    public List<Product> getAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        return products.stream().map(productMapper::toProduct).collect(Collectors.toList());
    }
}
