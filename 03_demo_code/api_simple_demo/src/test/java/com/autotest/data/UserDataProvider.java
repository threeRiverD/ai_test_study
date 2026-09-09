package com.autotest.data;

import org.testng.annotations.DataProvider;

/**
 * 用户模块数据驱动
 */
public class UserDataProvider {
    @DataProvider(name = "addUserData")
    public static Object[][] addUserData(){
        return new Object[][]{
                // 正常场景
                {"lisi", 25, 200, "操作成功"},
                // 用户名空，异常场景
                {"", 18, 500, "用户名不能为空"},
                // 年龄负数，异常场景
                {"wangwu", -5, 500, "用户年龄必须在0~120之间"}
        };
    }
}
