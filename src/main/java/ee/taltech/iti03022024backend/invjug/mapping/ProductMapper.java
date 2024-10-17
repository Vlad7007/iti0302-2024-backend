package ee.taltech.iti03022024backend.invjug.mapping;

import ee.taltech.iti03022024backend.invjug.dto.ProductDto;
import ee.taltech.iti03022024backend.invjug.repository.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductDto toProductDto(ProductEntity productEntity);
    ProductEntity toProductEntity(ProductDto productDto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ProductDto bookDto, @MappingTarget ProductEntity bookEntity);
}
