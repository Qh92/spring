package com.qinh.controller;

import com.qinh.entity.User;
import com.qinh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author Qh
 * @Date 2021/4/20 19:14
 * @Version 1.0
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据id查询用户
     *
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    public Mono<User> getUserById(@PathVariable int id){
        return userService.getUserById(id);
    }

    /**
     * 查询所有用户
     *
     * @return
     */
    @GetMapping("/users")
    public Flux<User> getUsers(){
        return userService.listUsers();
    }


    /**
     * 添加用户
     *
     * @param user
     * @return
     */
    @PostMapping("/saveuser")
    public Mono<Void> saveUser(@RequestBody User user){
        Mono<User> userMono = Mono.just(user);
        return userService.saveUserInfo(userMono);
    }
}
