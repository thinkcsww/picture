package com.applory.pictureserver.domain.user;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueNicknameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueNickname {
    String message() default "nickname must be unique";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
