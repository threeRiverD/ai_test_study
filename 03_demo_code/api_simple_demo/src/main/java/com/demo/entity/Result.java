package com.demo.entity;

import lombok.Data;

@Data
public class Result<T> {
    // 响应码 200成功，500失败
    private Integer code;
    // 提示信息
    private String msg;
    // 返回的数据
    private T data;

    /**
     * 成功返回，携带数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功返回，不带数据（比如删除操作）
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 失败返回（系统异常，code=500）
     */
    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }

    /**
     * 业务失败（自定义状态码，例如code=400）
     */
    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
