package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.dto.UserDto;
import ee.taltech.iti03022024backend.invjug.entities.Role;
import ee.taltech.iti03022024backend.invjug.entities.UserEntity;
import ee.taltech.iti03022024backend.invjug.errorhandling.NotFoundException;
import ee.taltech.iti03022024backend.invjug.mapping.UserMapper;
import ee.taltech.iti03022024backend.invjug.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private AdminService adminService;

    @Test
    public void test_get_all_users_returns_list_of_users() {
        UserEntity user1 = new UserEntity();
        UserEntity user2 = new UserEntity();
        List<UserEntity> users = List.of(user1, user2);

        UserDto userDto1 = new UserDto(1L, "user1", "user1@test.com", Role.ROLE_USER);
        UserDto userDto2 = new UserDto(2L, "user2", "user2@test.com", Role.ROLE_USER);

        given(userRepository.findAll()).willReturn(users);
        given(userMapper.toUserDto(user1)).willReturn(userDto1);
        given(userMapper.toUserDto(user2)).willReturn(userDto2);

        List<UserDto> result = adminService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(userDto1, userDto2);
    }


    @Test
    void test_update_user_success() {
        Long userId = 1L;
        UserDto inputDto = new UserDto(userId, "newUsername", "new@email.com", Role.ROLE_USER);
        UserEntity existingUser = new UserEntity();
        UserEntity updatedUser = new UserEntity();
        UserDto expectedDto = new UserDto(userId, "newUsername", "new@email.com", Role.ROLE_USER);

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.save(any(UserEntity.class))).willReturn(updatedUser);
        given(userMapper.toUserDto(updatedUser)).willReturn(expectedDto);

        UserDto result = adminService.updateUser(userId, inputDto);

        assertThat(result).isEqualTo(expectedDto);
        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
        verify(userMapper).toUserDto(updatedUser);
    }

    @Test
    void test_update_user_not_found() {
        Long userId = 999L;
        UserDto inputDto = new UserDto(userId, "username", "test@email.com", Role.ROLE_USER);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adminService.updateUser(userId, inputDto));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void test_get_user_by_id_returns_user_dto_when_valid_id_provided() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        UserDto expectedDto = new UserDto(userId, "test", "test@test.com", Role.ROLE_USER);
        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(userMapper.toUserDto(userEntity)).willReturn(expectedDto);

        UserDto result = adminService.getUserById(userId);

        assertThat(result).isEqualTo(expectedDto);
        verify(userRepository).findById(userId);
        verify(userMapper).toUserDto(userEntity);
    }

    @Test
    void test_get_user_by_id_throws_not_found_exception_when_invalid_id() {
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getUserById(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void test_delete_user_with_valid_id() {
        Long userId = 1L;
        willDoNothing().given(userRepository).deleteById(userId);

        adminService.deleteUser(userId);

        then(userRepository).should().deleteById(userId);
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    public void test_delete_user_with_nonexistent_id() {
        Long nonExistentId = 999L;
        willDoNothing().given(userRepository).deleteById(nonExistentId);

        adminService.deleteUser(nonExistentId);

        then(userRepository).should().deleteById(nonExistentId);
        then(userRepository).shouldHaveNoMoreInteractions();
    }
}