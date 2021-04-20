package com.qinh.service.impl;

import com.qinh.entity.User;
import com.qinh.service.UserService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Qh
 * @Date 2021/4/20 19:04
 * @Version 1.0
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private final Map<Integer,User> users = new HashMap<>();

    public UserServiceImpl() {
        this.users.put(1,new User("James","男",36));
        this.users.put(2,new User("Curry","男",32));
        this.users.put(3,new User("高圆圆","女",30));
    }

    @Override
    public Mono<User> getUserById(int id) {
        return Mono.justOrEmpty(this.users.get(id));
    }

    @Override
    public Flux<User> listUsers() {
        return Flux.fromIterable(this.users.values());
    }

    @Override
    public Mono<Void> saveUserInfo(Mono<User> userMono) {
        return userMono.doOnNext(o -> {
            this.users.put(users.size() + 1,o);
        }).thenEmpty(Mono.empty());
    }
}
