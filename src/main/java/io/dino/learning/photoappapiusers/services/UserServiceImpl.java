package io.dino.learning.photoappapiusers.services;

import io.dino.learning.photoappapiusers.data.UserEntity;
import io.dino.learning.photoappapiusers.repositories.UsersRepository;
import io.dino.learning.photoappapiusers.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    UsersRepository usersRepository;

    BCryptPasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder encoder) {
        this.usersRepository = usersRepository;
        this.encoder = encoder;
    }

    @Override
    public UserDto createUser(UserDto user) {
        user.setUserId(UUID.randomUUID().toString());
        user.setEncryptedPassword(encoder.encode(user.getPassword()));

        ModelMapper modelMapper = new ModelMapper();
        // To avoid model mapper getting confused when fields have similar names such as id and userid etc, set to strict
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        usersRepository.save(userEntity);

        UserDto persistedDto = modelMapper.map(userEntity, UserDto.class);
        return persistedDto;
    }
}
