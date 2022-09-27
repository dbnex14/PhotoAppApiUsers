package io.dino.learning.photoappapiusers.repositories;

import io.dino.learning.photoappapiusers.data.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
}
