package com.autotest.listener;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestResultListener implements ITestListener {

    // 用例失败触发
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("==================== 用例执行失败 ====================");
        System.out.println("用例名称：" + result.getName());
        System.out.println("异常信息：" + result.getThrowable().getMessage());
        System.out.println("======================================================");
    }
}
