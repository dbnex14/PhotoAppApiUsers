package io.dino.learning.photoappapiusers.services;

import io.dino.learning.photoappapiusers.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
    UserDto getUserDetailsByEmail(String email);
}
