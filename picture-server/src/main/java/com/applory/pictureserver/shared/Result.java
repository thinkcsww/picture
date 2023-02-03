package com.applory.pictureserver.shared;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Result<T> {
    public String code;
    public T data;
    public String message;

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.OK);
    }
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.OK, data);
    }

    public Result(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public Result(String code) {
        this.code = code;
    }

    static class ResultCode {
        public static final String OK = "0000";
        public static final String FAIL = "0001";
    }
}
