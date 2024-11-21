package com.mft.springsecurity.mapper;

import com.mft.springsecurity.dto.request.SignupRequest;
import com.mft.springsecurity.entity.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthMapper INSTANCE = Mappers.getMapper( AuthMapper.class );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    Auth map(SignupRequest dto);

}
