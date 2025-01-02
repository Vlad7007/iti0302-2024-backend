package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.dto.SupplierDto;
import ee.taltech.iti03022024backend.invjug.entities.SupplierEntity;
import ee.taltech.iti03022024backend.invjug.errorhandling.NotFoundException;
import ee.taltech.iti03022024backend.invjug.mapping.SupplierMapper;
import ee.taltech.iti03022024backend.invjug.repository.SupplierRepository;
import io.jsonwebtoken.lang.Collections;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierMapper supplierMapper;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    @Test
    void create_supplier_with_valid_data_returns_mapped_dto() {
        // Given
        SupplierDto inputDto = new SupplierDto(null, "Electronics", "electronics@electro.nics");
        SupplierEntity unmappedEntity = new SupplierEntity();
        SupplierEntity savedEntity = new SupplierEntity();
        SupplierDto expectedDto = new SupplierDto(1L, "Electronics", "electronics@electro.nics");

        given(supplierMapper.toSupplierEntity(inputDto)).willReturn(unmappedEntity);
        given(supplierRepository.save(unmappedEntity)).willReturn(savedEntity);
        given(supplierMapper.toSupplierDto(savedEntity)).willReturn(expectedDto);

        // When
        SupplierDto result = supplierService.createSupplier(inputDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(supplierMapper).toSupplierEntity(inputDto);
        verify(supplierRepository).save(unmappedEntity);
        verify(supplierMapper).toSupplierDto(savedEntity);
    }

    @Test
    void find_supplier_by_invalid_id_throws_not_found() {
        // Given
        Long nonExistentId = 999L;
        given(supplierRepository.findById(nonExistentId)).willReturn(Optional.empty());


        // When/Then
        assertThatThrownBy(() -> supplierService.findSupplierById(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Supplier not found");

        verify(supplierRepository).findById(nonExistentId);
        verifyNoInteractions(supplierMapper);
    }

    @Test
    void test_create_supplier_database_save_failure() {
        // Given
        SupplierDto inputDto = new SupplierDto(null, "Electronics", "electronics@electro.nics");
        SupplierEntity unmappedEntity = new SupplierEntity();

        given(supplierMapper.toSupplierEntity(inputDto)).willReturn(unmappedEntity);
        given(supplierRepository.save(unmappedEntity))
                .willThrow(new RuntimeException("Database save failed"));

        // When/Then
        assertThatThrownBy(() -> supplierService.createSupplier(inputDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database save failed");

        verify(supplierMapper).toSupplierEntity(inputDto);
        verify(supplierRepository).save(unmappedEntity);
    }

    @Test
    void test_returns_all_suppliers_when_multiple_exist() {
        // Given
        SupplierEntity supplier1 = new SupplierEntity();
        supplier1.setId(1L);
        supplier1.setName("Electronics");

        SupplierEntity supplier2 = new SupplierEntity();
        supplier2.setId(2L);
        supplier2.setName("Books");

        List<SupplierEntity> suppliers = List.of(supplier1, supplier2);

        SupplierDto supplierDto1 = new SupplierDto(1L, "Electronics", "Electronics desc");
        SupplierDto supplierDto2 = new SupplierDto(2L, "Books", "Books desc");

        given(supplierRepository.findAll()).willReturn(suppliers);
        given(supplierMapper.toSupplierDto(supplier1)).willReturn(supplierDto1);
        given(supplierMapper.toSupplierDto(supplier2)).willReturn(supplierDto2);

        // When
        List<SupplierDto> result = supplierService.getAllSuppliers();

        // Then
        AssertionsForInterfaceTypes.assertThat(result).hasSize(2);
        AssertionsForInterfaceTypes.assertThat(result).containsExactly(supplierDto1, supplierDto2);
    }

    // Returns empty list when no suppliers exist in repository
    @Test
    void test_returns_empty_list_when_no_suppliers_exist() {
        // Given
        given(supplierRepository.findAll()).willReturn(Collections.emptyList());

        // When
        List<SupplierDto> result = supplierService.getAllSuppliers();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void test_returns_supplier_dto_for_valid_id() {
        // Given
        Long id = 1L;
        SupplierEntity supplierEntity = new SupplierEntity();
        SupplierDto expectedDto = new SupplierDto(id, "Electronics", "electronics@electro.nics");

        given(supplierRepository.findById(id)).willReturn(Optional.of(supplierEntity));
        given(supplierMapper.toSupplierDto(supplierEntity)).willReturn(expectedDto);

        // When
        SupplierDto actualDto = supplierService.findSupplierById(id);

        // Then
        assertThat(actualDto).isEqualTo(expectedDto);
        verify(supplierRepository).findById(id);
        verify(supplierMapper).toSupplierDto(supplierEntity);
    }

    // Throws NotFoundException when supplier ID does not exist
    @Test
    void test_throws_not_found_exception_for_invalid_id() {
        // Given
        Long id = 999L;
        given(supplierRepository.findById(id)).willReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> supplierService.findSupplierById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Supplier not found");

        verify(supplierRepository).findById(id);
        verifyNoInteractions(supplierMapper);
    }

    @Test
    void test_update_supplier_success() {
        // Given
        Long supplierId = 1L;
        SupplierDto updatedDto = new SupplierDto(supplierId, "Updated Name", "updated@ema.il");
        SupplierEntity existingEntity = new SupplierEntity();
        SupplierEntity savedEntity = new SupplierEntity();
        SupplierDto expectedDto = new SupplierDto(supplierId, "Updated Name", "updated@ema.il");

        given(supplierRepository.findById(supplierId)).willReturn(Optional.of(existingEntity));
        given(supplierRepository.save(existingEntity)).willReturn(savedEntity);
        given(supplierMapper.toSupplierDto(savedEntity)).willReturn(expectedDto);

        // When
        SupplierDto result = supplierService.updateSupplier(supplierId, updatedDto);

        // Then
        then(supplierMapper).should().updateEntityFromDto(updatedDto, existingEntity);
        then(supplierRepository).should().save(existingEntity);
        assertThat(result).isEqualTo(expectedDto);
    }

    // Throws NotFoundException when trying to update non-existent supplier ID
    @Test
    void test_update_supplier_not_found() {
        // Given
        Long nonExistentId = 999L;
        SupplierDto updatedDto = new SupplierDto(nonExistentId, "Name", "e@ma.il");

        given(supplierRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> supplierService.updateSupplier(nonExistentId, updatedDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Supplier not found");

        then(supplierMapper).shouldHaveNoInteractions();
        then(supplierRepository).should(never()).save(any());
    }

    @Test
    void test_delete_existing_supplier_success() {
        // Given
        Long supplierId = 1L;
        SupplierEntity supplier = new SupplierEntity();
        supplier.setId(supplierId);
        given(supplierRepository.findById(supplierId)).willReturn(Optional.of(supplier));

        // When
        supplierService.deleteSupplier(supplierId);

        // Then
        then(supplierRepository).should().delete(supplier);
    }

    // Attempt to delete supplier with non-existent ID throws NotFoundException
    @Test
    void test_delete_nonexistent_supplier_throws_exception() {
        // Given
        Long nonExistentId = 999L;
        given(supplierRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When/Then
        assertThrows(NotFoundException.class, () -> supplierService.deleteSupplier(nonExistentId));
        then(supplierRepository).should(never()).delete(any());
    }

}