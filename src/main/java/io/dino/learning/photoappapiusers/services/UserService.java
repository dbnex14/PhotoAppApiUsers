package io.dino.learning.photoappapiusers.services;

import io.dino.learning.photoappapiusers.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.net.URISyntaxException;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
    UserDto getUserByUserId(String userId);
    UserDto getUserDetailsByEmail(String email);
}
