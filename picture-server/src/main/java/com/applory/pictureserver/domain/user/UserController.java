package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.domain.shared.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    private final UserService userService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto.VM createUser(@Valid @RequestBody UserDto.Create dto) {
        User newUser = userService.createUser(dto);
        return new UserDto.VM(newUser);
    }

    @GetMapping("/check-nickname")
    public void checkNickname(@RequestParam String nickname) {
        userService.checkNickname(nickname);
    }

    @GetMapping("/me")
    public UserDto.VM getUserMe(@CurrentUser User user) {
        return new UserDto.VM(userService.getUserMe());
    }

    @GetMapping("/seller")
    public Page<UserDto.SellerVM> getSellerUsers(@Valid UserDto.SearchSeller search, Pageable pageable) {
        return userService.getSellerUsers(search, pageable).map(UserDto.SellerVM::new);
    }

    @GetMapping("/client")
    public Page<UserDto.VM> getClientUsers(@Valid UserDto.SearchClient search, Pageable pageable) {
        return userService.getClientUsers(search, pageable).map(UserDto.VM::new);
    }

    @GetMapping("/seller/{id}")
    public UserDto.SellerVM getSellerUser(@PathVariable UUID id) {
        return userService.getSellerUser(id);
    }
}
