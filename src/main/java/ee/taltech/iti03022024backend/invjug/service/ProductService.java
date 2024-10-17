package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.controller.NotFoundException;
import ee.taltech.iti03022024backend.invjug.dto.ProductDto;
import ee.taltech.iti03022024backend.invjug.mapping.ProductMapper;
import ee.taltech.iti03022024backend.invjug.repository.ProductEntity;
import ee.taltech.iti03022024backend.invjug.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private static final String MESSAGE = "Product not found";

    public ProductService(ProductMapper productMapper, ProductRepository productRepository) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    public ProductDto createProduct(ProductDto productDto) {
        ProductEntity productEntity = productMapper.toProductEntity(productDto);
        ProductEntity savedEntity = productRepository.save(productEntity);

        return productMapper.toProductDto(savedEntity);
    }

    public List<ProductDto> getAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        return products.stream().map(productMapper::toProductDto).toList();
    }


    public ProductDto findProductById(Long id) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MESSAGE));
        return productMapper.toProductDto(productEntity);
    }

    public ProductDto updateProduct(Long id, ProductDto updatedProductDto) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MESSAGE));
        productMapper.updateEntityFromDto(updatedProductDto, productEntity);
        ProductEntity updatedEntity = productRepository.save(productEntity);
        return productMapper.toProductDto(updatedEntity);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException(MESSAGE);
        }
        productRepository.deleteById(id);
    }
}
