package com.qinh.service;

import com.qinh.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户操作接口
 *
 * @Author Qh
 * @Date 2021/4/20 18:59
 * @Version 1.0
 */
public interface UserService {

    /**
     * 根据id查询用户
     *
     * @param id
     * @return
     */
    Mono<User> getUserById(int id);

    /**
     * 查询所有用户
     *
     * @return
     */
    Flux<User> listUsers();

    /**
     * 添加用户
     *
     * @param user
     * @return
     */
    Mono<Void> saveUserInfo(Mono<User> user);


}
