package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.domain.shared.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    private final UserService userService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto.UserVM createUser(@Valid @RequestBody UserDto.Create dto) {
        User newUser = userService.createUser(dto);
        return new UserDto.UserVM(newUser);
    }

    @GetMapping("/me")
    public UserDto.UserVM getUserMe(@CurrentUser User user) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return null;
    }

    @GetMapping("")
    public Page<UserDto.UserVM> getSellerUsers(@Valid UserDto.SearchSeller search, Pageable pageable) {
        return userService.getSellerUsers(search, pageable).map(UserDto.UserVM::new);
    }

    @GetMapping("/client")
    public Page<UserDto.UserVM> getClientUsers(@Valid UserDto.SearchClient search, Pageable pageable) {
        return userService.getClientUsers(search, pageable).map(UserDto.UserVM::new);
    }
}
