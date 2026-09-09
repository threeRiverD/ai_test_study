package com.autotest.testcase.user.restassured;

import com.autotest.config.ConfigYamlUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserApiRestAssuredTest {
    private final String baseUrl = ConfigYamlUtil.getValue("api.baseUrl");
    private final String contentType = ConfigYamlUtil.getValue("api.contentType");

    // 每个用例执行之前执行（对应你熟悉的@BeforeMethod）
    @BeforeMethod
    void beforeTest(){
        System.out.println("===== 开始执行接口用例 =====");
    }

    // 每个用例执行之后执行
    @AfterMethod
    void afterTest(){
        System.out.println("===== 接口用例执行结束 =====");
    }

    //1.新增用户 正向
    @Test
    void testAddUserSuccess() {
        String jsonBody = "{\n" +
                "    \"username\": \"autotest01\",\n" +
                "    \"age\": 22\n" +
                "}";

        given()
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .post(baseUrl + "/user/add")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("msg", equalTo("操作成功"));
    }

    //2.新增用户：用户名为空 异常
    @Test
    void testAddUser_UsernameEmpty() {
        String jsonBody = "{\n" +
                "    \"username\": \"\",\n" +
                "    \"age\": 22\n" +
                "}";

        given()
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .post(baseUrl + "/user/add")
                .then()
                .body("code", equalTo(500))
                .body("msg", equalTo("用户名不能为空"));
    }

    //3.新增用户：年龄超出范围
    @Test
    void testAddUser_AgeInvalid() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put("username","testage");
        reqMap.put("age",130);
        String jsonBody = mapper.writeValueAsString(reqMap);
        given()
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .post(baseUrl + "/user/add")
                .then()
                .body("code", equalTo(500))
                .body("msg", equalTo("用户年龄必须在0~120之间"));
    }

    //4.查询用户：正常查询
    @Test
    void testGetUserSuccess() {
        given()
                .when()
                .get(baseUrl + "/user/2")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    //5.查询不存在ID
    @Test
    void testGetUser_NotExist() {
        given()
                .when()
                .get(baseUrl + "/user/9999")
                .then()
                .body("code", equalTo(500));
    }

    //6.更新用户PUT 正向
    @Test
    void testUpdateUserSuccess() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put("username","auto_testupdate");
        reqMap.put("age",10);
        String jsonBody = mapper.writeValueAsString(reqMap);
        given()
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .put(baseUrl + "/user/1")
                .then()
                .body("code", equalTo(200));
    }

    //7.更新不存在ID
    @Test
    void testUpdateUser_NotExistId() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put("username","test_update_no_id");
        reqMap.put("age",130);
        String jsonBody = mapper.writeValueAsString(reqMap);
        given()
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .put(baseUrl + "/user/9989")
                .then()
                .body("code", equalTo(500));
    }

    //8.删除用户DELETE 正向
    @Test
    void testDeleteUserSuccess() {
        given()
                .when()
                .delete(baseUrl + "/user/10")
                .then()
                .body("code", equalTo(200));
    }

    //9.删除不存在ID
    @Test
    void testDeleteUser_NotExistId() {
        given()
                .when()
                .delete(baseUrl + "/user/9999")
                .then()
                .body("code", equalTo(500));
    }
}
