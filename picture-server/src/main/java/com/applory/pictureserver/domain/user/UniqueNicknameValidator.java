package com.applory.pictureserver.domain.user;

import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueNicknameValidator implements ConstraintValidator<UniqueNickname, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext) {
        User inDB = userRepository.findByNickname(nickname);
        if (inDB == null) {
            return true;
        }

        return false;
    }
}
