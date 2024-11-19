package ee.taltech.iti03022024backend.invjug.mapping;

import ee.taltech.iti03022024backend.invjug.dto.UserDto;
import ee.taltech.iti03022024backend.invjug.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toUserDto(UserEntity userEntity);
    UserEntity toUserEntity(UserDto userDto);
}