package com.applory.pictureserver.shared;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Result {
    public String code;
    public Object data;
    public String message;

    public static Result success() {
        return new Result(ResultCode.OK);
    }
    public static Result success(Object data) {
        return new Result(ResultCode.OK, data);
    }

    public Result(String code, Object data) {
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
