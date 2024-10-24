package ee.taltech.iti03022024backend.invjug.controller;

import ee.taltech.iti03022024backend.invjug.dto.SupplierDto;
import ee.taltech.iti03022024backend.invjug.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/suppliers")
@RestController
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping
    public SupplierDto createSupplier(@RequestBody SupplierDto supplierDto) {
        return supplierService.createSupplier(supplierDto);
    }

    @GetMapping
    public List<SupplierDto> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }
}
