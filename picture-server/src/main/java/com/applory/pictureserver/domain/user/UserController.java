package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.domain.favorite.Favorite;
import com.applory.pictureserver.domain.favorite.FavoriteDTO;
import com.applory.pictureserver.domain.favorite.FavoriteService;
import com.applory.pictureserver.domain.user.querydto.SellerListVM;
import com.applory.pictureserver.shared.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final FavoriteService favoriteService;

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
    public Result getUserMe() {
        return Result.success(userService.getUserMe());
    }

    @GetMapping("/seller")
    public Result getSellerUsers(@Valid UserDto.SearchSeller search, Pageable pageable) {
        return Result.success(userService.getSellerUsers(search, pageable));
    }

    @GetMapping("/client")
    public Page<UserDto.VM> getClientUsers(@Valid UserDto.SearchClient search, Pageable pageable) {
        return userService.getClientUsers(search, pageable).map(UserDto.VM::new);
    }

    @GetMapping("/seller/{id}")
    public UserDto.SellerVM getSellerUser(@PathVariable UUID id, @RequestParam(required = false) UUID requesterId) {
        return userService.getSellerDetail(id, requesterId);
    }

    @PostMapping("/{id}/profile-image")
    public Result updateProfileImage(@PathVariable("id") UUID userId, UserDto.UpdateProfileImage dto) {
        userService.updateProfileImage(userId, dto);
        return Result.success();
    }

    @PostMapping("/favorites")
    public Result toggleFavorite(@RequestBody FavoriteDTO.Toggle body) {
        favoriteService.toggleFavorite(body);
        return Result.success();
    }
}
