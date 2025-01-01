package ee.taltech.iti03022024backend.invjug.mapping;

import ee.taltech.iti03022024backend.invjug.dto.SupplierDto;
import ee.taltech.iti03022024backend.invjug.entities.SupplierEntity;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {

    SupplierDto toSupplierDto(SupplierEntity supplierEntity);

    SupplierEntity toSupplierEntity(SupplierDto supplierDto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(SupplierDto updatedSupplierDto, @MappingTarget SupplierEntity supplierEntity);
}
