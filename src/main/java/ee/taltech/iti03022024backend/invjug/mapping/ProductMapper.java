package ee.taltech.iti03022024backend.invjug.mapping;

import ee.taltech.iti03022024backend.invjug.dto.ProductDto;
import ee.taltech.iti03022024backend.invjug.repository.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductDto toProductDto(ProductEntity productEntity);
    ProductEntity toProductEntity(ProductDto productDto);
}
