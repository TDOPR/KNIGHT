package com.defi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.defi.common.utils.Result;
import com.defi.entity.UserInfoEntity;
import com.defi.mapper.UserInfoDao;
import com.defi.service.UserInfoService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserInfoDao userInfoDao;

    @GetMapping("/test")
    public Result<UserInfoEntity> get(@Param("id") int id) {

        QueryWrapper<UserInfoEntity> userWrapper = new QueryWrapper<>();
        userWrapper.eq("id", id);
        UserInfoEntity createUserEntity = userInfoService.getOne(userWrapper);

        return new Result<UserInfoEntity>().ok(createUserEntity);
    }

//    @RequestMapping("/test")
//    public Result<UserInfoEntity> get(int id) {
//
//        UserInfoEntity createUserEntity = userInfoDao.selectUserInfoEntity(id);
//
//        return new Result<UserInfoEntity>().ok(createUserEntity);
//    }

    @RestController
    public class HelloController {

        @RequestMapping("/hello")
        public String say() {
            return "Hello SpringBoot!";
        }
    }
}

