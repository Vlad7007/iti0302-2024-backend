package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.dto.SupplierDto;
import ee.taltech.iti03022024backend.invjug.errorhandling.NotFoundException;
import ee.taltech.iti03022024backend.invjug.mapping.SupplierMapper;
import ee.taltech.iti03022024backend.invjug.entities.SupplierEntity;
import ee.taltech.iti03022024backend.invjug.repository.SupplierRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    
    private static final String SUPPLIER_MESSAGE = "Supplier not found";

    public SupplierDto createSupplier(SupplierDto supplierDto) {
        SupplierEntity supplierEntity = supplierMapper.toSupplierEntity(supplierDto);
        SupplierEntity savedEntity = supplierRepository.save(supplierEntity);

        return supplierMapper.toSupplierDto(savedEntity);
    }

    public List<SupplierDto> getAllSuppliers() {
        List<SupplierEntity> suppliers = supplierRepository.findAll();
        return suppliers.stream().map(supplierMapper::toSupplierDto).toList();
    }

    public SupplierDto getSupplier(Long id) {
        SupplierEntity supplierEntity = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SUPPLIER_MESSAGE));
        return supplierMapper.toSupplierDto(supplierEntity);
    }

    public SupplierDto updateSupplier(Long id, @Valid SupplierDto updatedSupplierDto) {
        SupplierEntity supplierEntity = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SUPPLIER_MESSAGE));
    
        supplierMapper.updateEntityFromDto(updatedSupplierDto, supplierEntity);
        SupplierEntity savedEntity = supplierRepository.save(supplierEntity);
    
        return supplierMapper.toSupplierDto(savedEntity);
    }

    public void deleteSupplier(Long id) {
        SupplierEntity supplierEntity = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SUPPLIER_MESSAGE));

        supplierRepository.delete(supplierEntity);
    }
}
