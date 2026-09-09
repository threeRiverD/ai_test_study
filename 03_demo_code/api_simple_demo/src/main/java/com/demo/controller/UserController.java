package com.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.demo.entity.Result;
import com.demo.entity.User;
import com.demo.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserMapper userMapper;

    /**
     * 新增用户 POST /user/add
     */
    @PostMapping("/add")
    public Result<Integer> add(@RequestBody User user){
        if (user.getUsername() == null || user.getUsername().trim().equals("")){
            throw new RuntimeException("用户名不能为空");
        }
        if (user.getAge() < 0 || user.getAge() >120){
            throw new RuntimeException("用户年龄必须在1~120之间");
        }
        int rows = userMapper.insert(user);
        if (rows == 0){
            throw new RuntimeException("新增用户失败");
        }
        return Result.success(rows);
    }

    /**
     * 根据id查询 GET /user/{id}
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Integer id){
        if (id == null || id <=0) {
            throw new RuntimeException("id不合法");
        }
        return Result.success(userMapper.selectById(id));
    }

    /**
     * 查询全部列表 GET /user/list
     */
    @GetMapping("/list")
    public Result<List<User>> list(){
        return Result.success(userMapper.selectList(new LambdaQueryWrapper<>()));
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody User user){
        if (user.getUsername() == null || user.getUsername().trim().equals("")){
            throw new RuntimeException("用户名不能为空");
        }
        if (user.getAge() < 0 || user.getAge() >120){
            throw new RuntimeException("用户年龄必须在1~120之间");
        }
        User updateEntity = new User();
        updateEntity.setUsername(user.getUsername());
        updateEntity.setAge(user.getAge());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId,user.getId());
        int rows = userMapper.update(updateEntity,wrapper);
        if (rows == 0){
            throw new RuntimeException("未找到ID="+user.getId()+"的用户,更新失败");
        }
        return Result.success("影响行数:"+rows);
    }
    /**
     * 删除 DELETE /user/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Integer id){
        if (id == null || id <=0) {
            throw new RuntimeException("id不合法");
        }
        int  rows = userMapper.deleteById(id);
        if (rows ==0){
            throw new RuntimeException("没有找到id="+id+"的用户");
        }
        return Result.success(rows);
    }
}
