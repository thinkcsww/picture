package com.applory.pictureserver.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<UserVM> createUser(@Valid @RequestBody UserDto.Create dto) {
        User newUser = userService.createUser(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserVM(newUser));
    }

    @GetMapping("")
    public Object getUsers() {
        return userRepository.findAll();
    }
}
