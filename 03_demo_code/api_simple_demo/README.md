# SpringBoot 用户管理接口实战项目
> 测试开发 Demo 项目，用于演示接口开发、接口功能测试、接口自动化、性能压测。
> 适配简历项目：可用于说明接口巡检平台底层能力，基于Java + SpringBoot + MyBatis-Plus。

## ✨ 项目亮点
1. 统一封装返回对象 Result，所有接口响应格式标准化，便于自动化脚本做JSON断言
2. 全局异常处理器，统一捕获业务异常，无需在每个接口编写 try-catch
3. 入参合法性校验，拦截非法参数，友好的错误提示
4. 覆盖正向业务场景 + 多种异常边界场景，符合测试思维
5. 可扩展：可接入RestAssured自动化、JMeter性能压测，可改造为接口巡检平台

## 🛠️ 技术栈
- 后端框架：SpringBoot 2.x
- ORM框架：MyBatis-Plus
- 数据库：MySQL 8.0
- 测试工具：Postman、RestAssured、JMeter
- 版本管理：Git + GitHub

## 📋 环境准备
1. 本地启动 MySQL，新建数据库 `test_demo`，创建 user 表
```sql
CREATE TABLE `user` (
  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `age` INT COMMENT '年龄'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```
2. 修改 `application.yml`，填写本地 MySQL 地址、账号、密码
3. 启动 SpringBoot 主程序，默认访问端口：`8080`

## 📖 接口文档

基础前缀地址：`http://127.0.0.1:8080`

### 1. 新增用户

- 请求方式：POST
- 接口地址：`/user/add`
- Content-Type：application/json

✅ 正常请求 Body

```
{
    "username": "222",
    "age": 25
}
```

✅ 成功响应

```
{
    "code": 200,
    "msg": "操作成功",
    "data": 1
}
```

❌ 异常场景 1：用户名为空

```
{
    "username": "",
    "age": 25
}
```

响应

```
{
    "code": 500,
    "msg": "用户名不能为空",
    "data": null
}
```

❌ 异常场景 2：age=-1

```
{
    "username": "list",
    "age": -1
}
```

响应

```
{
    "code": 500,
    "msg": "用户年龄必须在1~120之间",
    "data": null
}
```

❌ 异常场景 3：age=130

```
{
    "username": "list",
    "age": 130
}
```

响应

```
{
    "code": 500,
    "msg": "用户年龄必须在1~120之间",
    "data": null
}
```

---

### 2. 根据 ID 查询单个用户

- 请求方式：GET
- 接口地址：`/user/{id}`

✅ 正常请求：`GET /user/1`

```
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "id": 1,
        "username": "zhangsanuuu",
        "age": 22100,
        "createTime": "2026-09-08T11:56:43"
    }
}
```

❌ 异常 1：id 非法，`GET /user/-1`

```
{
    "code": 500,
    "msg": "id不合法",
    "data": null
}
```

❌ 异常 2：id 不存在，`GET /user/9999`

```
{
    "code": 500,
    "msg": "没有找到id=999的用户",
    "data": null
}
```

---

### 3. 查询全部用户列表

- 请求方式：GET
- 接口地址：`/user/list`

✅ 成功响应

```
{
    "code": 200,
    "msg": "操作成功",
    "data": [
        {
            "id": 1,
            "username": "zhangsanuuu",
            "age": 22100,
            "createTime": "2026-09-08T11:56:43"
        },
        {
            "id": 2,
            "username": "zhangsan",
            "age": 22,
            "createTime": "2026-09-08T11:56:59"
        },
        {
            "id": 3,
            "username": "zhangsanuuu",
            "age": 22,
            "createTime": "2026-09-08T16:09:48"
        },
        {
            "id": 4,
            "username": "zhangsanuuu",
            "age": 25,
            "createTime": "2026-09-09T10:11:35"
        },
        {
            "id": 5,
            "username": "222",
            "age": 25,
            "createTime": "2026-09-09T10:12:34"
        }
    ]
}
```

---

### 4. 修改用户

- 请求方式：PUT
- 接口地址：`/user/{id}`

✅ 请求 Body

```
{
  "username":"zhangsanuuu",
  "age":100
}

```

✅ 成功响应

```
{
    "code": 200,
    "msg": "操作成功",
    "data": "影响行数:1"
}
```

❌ 异常：id 不存在 `PUT /user/1111

```
{
  "username":"zhangsanuuu",
  "age":100
}

```

响应

```
{
    "code": 500,
    "msg": "未找到ID=1111的用户,更新失败",
    "data": null
}
```
❌ 异常：id 不合法 `PUT /user/-1

```
{
  "username":"zhangsanuuu",
  "age":100
}

```

响应

```
{
    "code": 500,
    "msg": "id 不合法",
    "data": null
}
```
---

### 5. 删除用户

- 请求方式：DELETE
- 接口地址：`/user/{id}`

✅ 正常请求：`DELETE /user/1`

```
{
"code": 200,
"msg": "操作成功",
"data": 1
}
```

❌ 异常 1：id 非法 `DELETE /user/-1`

```
{
    "code": 500,
    "msg": "id不合法",
    "data": null
}
```

❌ 异常 2：id 不存在 `DELETE /user/111`

```
{
    "code": 500,
    "msg": "没有找到id=111的用户",
    "data": null
}
```

## 🧪 功能测试说明

> 
> 使用 Postman 手工测试，覆盖场景：
> 
> 
> - 正向业务流程：新增 → 查询 → 修改 → 查询 → 删除
> - 边界异常：空参数、非法数值、不存在 ID
> - 校验每个接口返回的 code、msg、data 字段

## 🤖 后续迭代计划

1. 接入 RestAssured，编写 Java 接口自动化用例，实现接口回归自动化
2. JMeter 性能压测，测试查询接口 TPS、响应时间
3. 扩展能力：定时巡检、结果报告、失败告警（对标测试平台接口巡检模块）

## 📂 项目目录结构
src
├── main
│   ├── java/com/demo
│   │   ├── controller      # Controller接口层
│   │   ├── entity          # 实体类（User、Result统一返回体）
│   │   ├── mapper          # Mapper数据库层
│   │   ├── service         # 业务逻辑层
│   │   └── util            # 全局工具-全局异常处理器
│   └── resources
│       └── application.yml # 项目配置文件
└── test                    # 自动化测试代码（后续添加）
pom.xml
.gitignore
README.md
