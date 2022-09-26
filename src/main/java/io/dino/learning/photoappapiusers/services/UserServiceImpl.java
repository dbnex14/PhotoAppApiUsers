package io.dino.learning.photoappapiusers.services;

import io.dino.learning.photoappapiusers.data.UserEntity;
import io.dino.learning.photoappapiusers.repositories.UsersRepository;
import io.dino.learning.photoappapiusers.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    UsersRepository usersRepository;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDto createUser(UserDto user) {
        user.setUserId(UUID.randomUUID().toString());

        ModelMapper modelMapper = new ModelMapper();
        // To avoid model mapper getting confused when fields have similar names such as id and userid etc, set to strict
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        userEntity.setEncryptedPassword("test");
        usersRepository.save(userEntity);

        UserDto persistedDto = modelMapper.map(userEntity, UserDto.class);
        return persistedDto;
    }
}
