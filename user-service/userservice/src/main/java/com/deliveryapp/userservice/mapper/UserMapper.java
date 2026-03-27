package com.deliveryapp.userservice.mapper;

import com.deliveryapp.userservice.dto.response.UserResponse;
import com.deliveryapp.userservice.entity.UserEntity;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse mapEntityToResponse(UserEntity user);

    List<UserResponse> mapEntityListToResponseList(List<UserEntity> users);
}