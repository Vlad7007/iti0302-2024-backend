package ee.taltech.iti03022024backend.invjug.mapping;

import ee.taltech.iti03022024backend.invjug.dto.ProductDto;
import ee.taltech.iti03022024backend.invjug.entities.CategoryEntity;
import ee.taltech.iti03022024backend.invjug.entities.ProductEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "categoryIds", source = "categories", qualifiedByName = "mapCategoriesToIds")
    ProductDto toProductDto(ProductEntity productEntity);

    @Mapping(target = "categories", ignore = true)
    ProductEntity toProductEntity(ProductDto productDto);

    @Named("mapCategoriesToIds")
    default List<Long> mapCategoriesToIds(List<CategoryEntity> categories) {
        return categories.stream()
                .map(CategoryEntity::getId)
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ProductDto productDto, @MappingTarget ProductEntity productEntity);
}
