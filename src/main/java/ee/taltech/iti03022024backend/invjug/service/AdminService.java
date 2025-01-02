package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.dto.UserDto;
import ee.taltech.iti03022024backend.invjug.entities.UserEntity;
import ee.taltech.iti03022024backend.invjug.errorhandling.NotFoundException;
import ee.taltech.iti03022024backend.invjug.mapping.UserMapper;
import ee.taltech.iti03022024backend.invjug.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String USER_MESSAGE = "User not found";

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_MESSAGE));

        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setRole(userDto.role());

        UserEntity updatedUser = userRepository.save(user);
        return userMapper.toUserDto(updatedUser);
    }

    public UserDto getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_MESSAGE));
        return userMapper.toUserDto(userEntity);
    }

    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_MESSAGE));
        userRepository.delete(user);
    }
}