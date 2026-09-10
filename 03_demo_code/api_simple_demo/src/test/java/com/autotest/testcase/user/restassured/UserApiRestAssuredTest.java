package com.autotest.testcase.user.restassured;

import com.autotest.config.ConfigYamlUtil;
import com.autotest.data.UserDataProvider;
import com.autotest.listener.TestResultListener;
import com.autotest.util.JdbcUtil;
import com.autotest.util.RestApiUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Listeners(TestResultListener.class)
@Feature("用户模块接口测试")
public class UserApiRestAssuredTest {
    private final String baseUrl = ConfigYamlUtil.getValue("api.baseUrl");

    // 每个用例执行之前执行（对应你熟悉的@BeforeMethod）
    @BeforeMethod
    void beforeTest() {
        System.out.println("===== 开始执行接口用例 =====");
    }

    // 每个用例执行之后执行
    @AfterMethod
    void afterTest() {
        System.out.println("===== 接口用例执行结束 =====");
    }

    //1.新增用户 正向
    @Test
    @Story("新增用户")
    @Description("新增用户,数据库校验")
    void testAddUserSuccess() {
        String jsonBody = "{\n" +
                "    \"username\": \"autotest01\",\n" +
                "    \"age\": 22\n" +
                "}";

        String url = baseUrl + "/user/add";
        Response resp = RestApiUtil.postJson(url, jsonBody);
        resp
                .then()
                .body("code", equalTo(200))
                .body("msg", equalTo("操作成功"));
        int expectCode = resp.getStatusCode();
        // 2.正向场景才做数据库校验；异常场景不校验库数据
        if (expectCode == 200) {
            // 查询数据库，校验是否成功落库
            Map<String, Object> dbResult = JdbcUtil.queryOne("select * from user where username = '"+"autotest01'");
            // 校验库中用户名、年龄和传入一致
            assert dbResult.get("username").equals("autotest01");
            assert dbResult.get("age").equals(22);
        }
    }

    //2.新增用户：用户名为空 异常
    @Test
    @Story("新增用户")
    @Description("新增用户,用户名为空 异常")
    void testAddUser_UsernameEmpty() {
        String jsonBody = "{\n" +
                "    \"username\": \"\",\n" +
                "    \"age\": 22\n" +
                "}";
        String url = baseUrl + "/user/add";
        Response resp = RestApiUtil.postJson(url, jsonBody);
        resp
                .then()
                .body("code", equalTo(500))
                .body("msg", equalTo("用户名不能为空"));
    }

    //3.新增用户：年龄超出范围
    @Test
    @Story("新增用户")
    @Description("新增用户,年龄超出范围")
    void testAddUser_AgeInvalid() throws JsonProcessingException {

        String jsonBody = addUser("testadduser",130);
        String url = baseUrl + "/user/add";
        Response resp = RestApiUtil.postJson(url, jsonBody);
        resp
                .then()
                .body("code", equalTo(500))
                .body("msg", equalTo("用户年龄必须在0~120之间"));
    }

    /**
     * 新增用户数据封装
     * @param username
     * @param age
     * @return
     * @throws JsonProcessingException
     */
    String addUser(String username, int age) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("username", username);
        reqMap.put("age", age);
        String jsonBody = mapper.writeValueAsString(reqMap);
        return jsonBody;
    }

    //4.查询用户：正常查询
    @Test
    @Story("查询用户")
    @Description("查询用户,正常查询")
    void testGetUserSuccess() throws JsonProcessingException {
        //新增一个测试数据,然后查询到他的id,在删除
        String jsonbody = addUser("testSearch",11);
        String adduserUrl = baseUrl + "/user/add";
        Response adduserRes = RestApiUtil.postJson(adduserUrl, jsonbody);
        if (adduserRes.getStatusCode() == 200){
            Map<String, Object> dbResult = JdbcUtil.queryOne("select * from user where username = '"+"testSearch'");
            String testSearchId = dbResult.get("id").toString();
            String url = baseUrl + "/user/"+testSearchId;
            Response resp = RestApiUtil.get(url);
            resp
                    .then()
                    .statusCode(200)
                    .body("code", equalTo(200));
        }else {
            throw new RuntimeException("查询用户——前置用例新增用户失败");
        }
    }

    //5.查询不存在ID
    @Test
    @Story("新增用户")
    @Description("新增用户,查询不存在的用户")
    void testGetUser_NotExist() {
        String url = baseUrl + "/user/9988";
        Response resp = RestApiUtil.get(url);
        resp
                .then()
                .body("code", equalTo(500));
    }

    //6.更新用户PUT 正向
    @Test
    @Story("更新用户")
    @Description("更新用户,正常场景")
    void testUpdateUserSuccess() throws JsonProcessingException {
        //新增一个测试数据,然后查询到他的id,在删除
        String jsonbody = addUser("testUpdate",11);
        String adduserUrl = baseUrl + "/user/add";
        Response adduserRes = RestApiUtil.postJson(adduserUrl, jsonbody);
        if (adduserRes.getStatusCode() == 200){
            Map<String, Object> dbResult = JdbcUtil.queryOne("select * from user where username = '"+"testUpdate'");
            String testUpdateId = dbResult.get("id").toString();
            String url = baseUrl + "/user/"+testUpdateId;
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("username", "auto_testupdate");
            reqMap.put("age", 10);
            String jsonBody = mapper.writeValueAsString(reqMap);
            Response resp = RestApiUtil.putJson(url, jsonBody);
            resp
                    .then()
                    .body("code", equalTo(200));
            String durl = baseUrl + "/user/"+testUpdateId;
            Response dresp = RestApiUtil.delete(url);
            resp
                    .then()
                    .body("code", equalTo(200));
        }else {
            throw new RuntimeException("删除用户——前置用例新增用户失败");
        }

    }

    //7.更新不存在ID
    @Test
    @Story("更新用户用户")
    @Description("新增用户,更新不存在的用户id")
    void testUpdateUser_NotExistId() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("username", "test_update_no_id");
        reqMap.put("age", 130);
        String jsonBody = mapper.writeValueAsString(reqMap);
        String url = baseUrl + "/user/9989";
        Response resp = RestApiUtil.putJson(url, jsonBody);
        resp
                .then()
                .body("code", equalTo(500));
    }

    //8.删除用户DELETE 正向
    @Test
    @Story("删除用户")
    @Description("删除用户用户")
    void testDeleteUserSuccess() throws JsonProcessingException {
        //新增一个测试数据,然后查询到他的id,在删除
        String jsonbody = addUser("testDelete",11);
        String adduserUrl = baseUrl + "/user/add";
        Response adduserRes = RestApiUtil.postJson(adduserUrl, jsonbody);
        if (adduserRes.getStatusCode() == 200){
            Map<String, Object> dbResult = JdbcUtil.queryOne("select * from user where username = '"+"testDelete'");
            String testDeleteId = dbResult.get("id").toString();
            String url = baseUrl + "/user/"+testDeleteId;
            Response resp = RestApiUtil.delete(url);
            resp
                    .then()
                    .body("code", equalTo(200));
        }else {
            throw new RuntimeException("删除用户——前置用例新增用户失败");
        }

    }

    //9.删除不存在ID
    @Test
    @Story("删除用户")
    @Description("删除用户,删除不存在的用户id")
    void testDeleteUser_NotExistId() {
        String url = baseUrl + "/user/2";
        Response resp = RestApiUtil.delete(url);
        resp
                .then()
                .body("code", equalTo(500));
    }

    /**
     * 用户数据驱动测试添加用户
     */
    @Test(dataProvider="addUserData",dataProviderClass = UserDataProvider.class)
    @Story("用户驱动测试新增用户")
    @Description("新增用户")
    void testAddUserData(String username, Integer age, int expectCode, String expectMsg){
// 拼接json请求体
        String reqBody = String.format("{\"username\":\"%s\",\"age\":%d}", username, age);
        String url = baseUrl + "/user/add";

        Response resp = RestApiUtil.postJson(url, reqBody);

        // 断言
        resp.then()
                .body("code", equalTo(expectCode))
                .body("msg", equalTo(expectMsg));

    }
}
