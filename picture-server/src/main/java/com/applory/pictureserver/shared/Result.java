package com.applory.pictureserver.shared;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Result<T> {
    public T data;
    public String message;

    public static <T> Result<T> of (T data) {
        return new Result<>(data);
    }

    public Result(T data) {
        this.data = data;
    }
}
