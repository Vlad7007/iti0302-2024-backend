
package ee.taltech.iti03022024backend.invjug.controller;

import ee.taltech.iti03022024backend.invjug.dto.SupplierDto;
import ee.taltech.iti03022024backend.invjug.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/suppliers")
@PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
@RestController
@RequiredArgsConstructor
@Tag(name = "Supplier", description = "Suppliers management APIs")
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping
    @Operation(
            summary = "Add a new supplier to the system",
            description = "Creates a new supplier record based on the provided details."
    )
    @ApiResponse(responseCode = "200", description = "Supplier added successfully")
    public SupplierDto createSupplier(@Valid @RequestBody SupplierDto supplierDto) {
        return supplierService.createSupplier(supplierDto);
    }

    @GetMapping
    @Operation(
            summary = "Get all suppliers",
            description = "Fetches all the suppliers."
    )
    @ApiResponse(responseCode = "200", description = "Successfully received list of suppliers")
    public List<SupplierDto> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }


    @Operation(summary = "Get a supplier by ID", description = "Fetches a supplier by its unique identifier.")
    @ApiResponse(responseCode = "200", description = "Supplier fetched successfully.")
    @ApiResponse(responseCode = "404", description = "Supplier not found.")
    @GetMapping("/{id}")
    public SupplierDto getSupplier(@PathVariable Long id) {
        return supplierService.findSupplierById(id);
    }

    @Operation(summary = "Update an existing supplier", description = "Updates a supplier by its ID with provided information.")
    @ApiResponse(responseCode = "200", description = "Supplier updated successfully.")
    @ApiResponse(responseCode = "404", description = "Supplier not found.")
    @ApiResponse(responseCode = "400", description = "Invalid supplier data.")
    @PutMapping("/{id}")
    public SupplierDto updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierDto supplierDto) {
        return supplierService.updateSupplier(id, supplierDto);
    }

    @Operation(summary = "Delete a supplier", description = "Deletes a supplier by its unique identifier.")
    @ApiResponse(responseCode = "204", description = "Supplier deleted successfully.")
    @ApiResponse(responseCode = "404", description = "Supplier not found.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
    }
}
