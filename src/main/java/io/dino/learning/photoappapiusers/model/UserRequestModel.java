package io.dino.learning.photoappapiusers.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserRequestModel {
    @NotNull(message = "firstName cannot be null")
    @Size(min = 2, message = "firstName must be at least 2 characters")
    public String firstName;

    @NotNull(message = "lastName cannot be null")
    @Size(min = 2, message = "lastName must be at least 2 characters")
    public String lastName;

    @NotNull(message = "email cannot be null")
    @Email
    public String email;

    @NotNull(message = "password cannot be null")
    @Size(min = 8, max = 16, message = "password must be between 8 - 16 characters")
    public String password;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
