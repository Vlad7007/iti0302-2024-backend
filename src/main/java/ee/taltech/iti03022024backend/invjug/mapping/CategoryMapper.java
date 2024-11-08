package ee.taltech.iti03022024backend.invjug.mapping;

import ee.taltech.iti03022024backend.invjug.dto.CategoryDto;
import ee.taltech.iti03022024backend.invjug.entities.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    CategoryDto toCategoryDto(CategoryEntity categoryEntity);
    CategoryEntity toCategoryEntity(CategoryDto categoryDto);
}
