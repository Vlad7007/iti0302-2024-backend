package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.dto.SupplierDto;
import ee.taltech.iti03022024backend.invjug.mapping.SupplierMapper;
import ee.taltech.iti03022024backend.invjug.entities.SupplierEntity;
import ee.taltech.iti03022024backend.invjug.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierDto createSupplier(SupplierDto supplierDto) {
        SupplierEntity supplierEntity = supplierMapper.toSupplierEntity(supplierDto);
        SupplierEntity savedEntity = supplierRepository.save(supplierEntity);

        return supplierMapper.toSupplierDto(savedEntity);
    }

    public List<SupplierDto> getAllSuppliers() {
        List<SupplierEntity> suppliers = supplierRepository.findAll();
        return suppliers.stream().map(supplierMapper::toSupplierDto).toList();
    }
}
