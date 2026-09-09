package com.autotest.util;

import com.autotest.config.ConfigYamlUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class RestApiUtil {
    private static final String CONTENT_TYPE = ConfigYamlUtil.getValue("api.contentType");

    // ========== 底层公共方法（抽离重复逻辑） ==========
    private static Response sendRequest(String method, String url, Map<String,String> headers, String jsonBody){
        return RestAssured.given()
                .headers(headers)
                .body(jsonBody)
                .when()
                .request(method, url);
    }

    // ========== POST 新增 ==========
    public static Response postJson(String url, String jsonBody) {
        return postJson(url, new HashMap<>(), jsonBody);
    }
    public static Response postJson(String url, Map<String,String> headers, String jsonBody) {
        headers.putIfAbsent("Content-Type", CONTENT_TYPE);
        return sendRequest("POST", url, headers, jsonBody);
    }

    // ========== PUT 全量更新 ==========
    public static Response putJson(String url, String jsonBody) {
        return putJson(url, new HashMap<>(), jsonBody);
    }
    public static Response putJson(String url, Map<String,String> headers, String jsonBody) {
        headers.putIfAbsent("Content-Type", CONTENT_TYPE);
        return sendRequest("PUT", url, headers, jsonBody);
    }

    // ========== GET 查询 ==========
    public static Response get(String url) {
        return get(url, new HashMap<>());
    }
    public static Response get(String url, Map<String,String> headers) {
        return sendRequest("GET", url, headers, null);
    }

    // ========== DELETE 删除 ==========
    public static Response delete(String url) {
        return delete(url, new HashMap<>());
    }
    public static Response delete(String url, Map<String,String> headers) {
        return sendRequest("DELETE", url, headers, null);
    }
}
