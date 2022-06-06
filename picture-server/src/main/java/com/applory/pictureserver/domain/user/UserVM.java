package com.applory.pictureserver.domain.user;

import lombok.Data;

import java.util.UUID;

@Data
public class UserVM {
    private UUID id;

    public UserVM(User user) {
        this.id = user.getId();
    }
}
