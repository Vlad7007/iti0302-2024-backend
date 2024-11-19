package ee.taltech.iti03022024backend.invjug.controller;

import ee.taltech.iti03022024backend.invjug.dto.UserDto;
import ee.taltech.iti03022024backend.invjug.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Fetches a list of all users.")
    @ApiResponse(responseCode = "200", description = "Successfully received list of users")
    public List<UserDto> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user", description = "Updates the details of a user by their ID.")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public UserDto updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserDto userDto) {
        return adminService.updateUser(id, userDto);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get a user by ID", description = "Fetches a user by their ID.")
    @ApiResponse(responseCode = "200", description = "User found successfully")
    public UserDto getUserById(@PathVariable("id") Long id) {
        return adminService.getUserById(id);
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user by their ID.")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    public void deleteUser(@PathVariable("id") Long id) {
        adminService.deleteUser(id);
    }
}