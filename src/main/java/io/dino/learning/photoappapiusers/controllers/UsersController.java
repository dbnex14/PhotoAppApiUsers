package io.dino.learning.photoappapiusers.controllers;

import io.dino.learning.photoappapiusers.model.UserRequestModel;
import io.dino.learning.photoappapiusers.model.UserResponseModel;
import io.dino.learning.photoappapiusers.services.UserService;
import io.dino.learning.photoappapiusers.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private Environment environment;

    @Autowired
    UserService userService;

    @GetMapping("/status/check")
    public String status() {
        return "Working on port " + environment.getProperty("local.server.port");
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<UserResponseModel> createUser(@RequestBody UserRequestModel user) {
        ModelMapper modelMapper = new ModelMapper();
        // To avoid model mapper getting confused when fields have similar names such as id and userid etc, set to strict
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(user, UserDto.class);

        UserDto persistedUserDto = userService.createUser(userDto);

        UserResponseModel response = modelMapper.map(persistedUserDto, UserResponseModel.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
