package ee.taltech.iti03022024backend.invjug.mapping;

import ee.taltech.iti03022024backend.invjug.dto.SupplierDto;
import ee.taltech.iti03022024backend.invjug.repository.SupplierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {

    SupplierDto toSupplierDto(SupplierEntity supplierEntity);

    SupplierEntity toSupplierEntity(SupplierDto supplierDto);
}
