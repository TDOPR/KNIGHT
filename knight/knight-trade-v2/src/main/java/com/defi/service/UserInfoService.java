package com.defi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.defi.entity.UserInfoEntity;
import com.defi.utils.PageUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户表
 *
 * @date 2021-12-24 11:09:24
 */
public interface UserInfoService extends IService<UserInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //注册用户
    void createUser(String userAddress,String inviteAddress) throws Exception;

    void insertTreePath(Map<String, Object> map);

    void addTeamPerformanceById(int uid, BigDecimal amount);

    void subtractTeamPerformanceById(int uid, BigDecimal amount);

    List<UserInfoEntity> selectSuperUserList();

}

