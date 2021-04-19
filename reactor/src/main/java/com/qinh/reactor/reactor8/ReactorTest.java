package com.qinh.reactor.reactor8;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-19-23:48
 */
public class ReactorTest {
    public static void main(String[] args) {
        //just方法直接声明
        Flux.just(1,2,3,5).subscribe(System.out::println);
        Mono.just(1).subscribe(System.out::println);

        //其它方法
        Integer[] arr = {1,2,3,4};
        Flux.fromArray(arr);

        List<Integer> list = Arrays.asList(arr);
        Flux.fromIterable(list);

        Stream<Integer> stream = list.stream();
        Flux.fromStream(stream);
    }
}
