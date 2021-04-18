package com.qinh.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-18-22:27
 */
public class UserLog {

    private static final Logger logger = LoggerFactory.getLogger(UserLog.class);

    public static void main(String[] args) {
        logger.info("hello log4j2");
        logger.warn("hello log4j2");
    }
}
