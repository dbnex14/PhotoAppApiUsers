package io.dino.learning.photoappapiusers.services;

import feign.FeignException;
import io.dino.learning.photoappapiusers.data.AlbumsServiceClient;
import io.dino.learning.photoappapiusers.data.UserEntity;
import io.dino.learning.photoappapiusers.model.AlbumResponseModel;
import io.dino.learning.photoappapiusers.repositories.UsersRepository;
import io.dino.learning.photoappapiusers.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpMethod;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    UsersRepository usersRepository;

    BCryptPasswordEncoder encoder;
    //RestTemplate restTemplate;
    AlbumsServiceClient albumsServiceClient;
    Environment environment;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder encoder, /*RestTemplate restTemplate,*/ AlbumsServiceClient albumsServiceClient, Environment environment) {
        this.usersRepository = usersRepository;
        this.encoder = encoder;
        //this.restTemplate = restTemplate;
        this.albumsServiceClient = albumsServiceClient;
        this.environment = environment;
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

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = usersRepository.findUserByUserId(userId);

        if (userEntity == null) throw new UsernameNotFoundException("User not found");

        // The Uri below uses name of albums service 'albums-ws' so RestTemplate can load balnace requests to
        // this microservices.  "albums-ws" is the name under which this microservice is registered in Eureka Discovery Service
        //URI albumsUri = URI.create(String.format(environment.getProperty("albums.url"), userId));

        /*
         // Using RestTemplate
        String albumsUri = String.format(environment.getProperty("albums.url"), userId);
        ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(
                albumsUri
                , HttpMethod.GET
                , null
                , new ParameterizedTypeReference<List<AlbumResponseModel>>() {});
        List<AlbumResponseModel> albumsList = albumsListResponse.getBody();
        */

        // Using Feign client
        List<AlbumResponseModel> albumsList = null;
//        try {
                logger.info("Before calling AlbumsServiceClient.getAlbums()");
                albumsList = albumsServiceClient.getAlbums(userId); //Feign
                logger.info("After calling AlbumsServiceClient.getAlbums()");
//        } catch(FeignException e) {
//            logger.error(e.getLocalizedMessage());
//        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        userDto.setAlbums(albumsList);

        return userDto;
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = usersRepository.findByEmail(email); // we use email as username in this project

        if (userEntity == null) throw new UsernameNotFoundException(email);
        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = usersRepository.findByEmail(username); // we use email as username in this project

        if (userEntity == null) throw new UsernameNotFoundException(username);

        return new User(
                userEntity.getEmail()
                , userEntity.getEncryptedPassword()
                , true // is account enabled or not (if set to false, email verification functionality will not allow login in until verified)
                , true
                , true
                , true
                , new ArrayList<>());
    }
}
