package com.defi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.defi.entity.RewardFreezeRobotEntity;

import java.util.Map;

/**
 *
 */
public interface RewardFreezeRobotService extends IService<RewardFreezeRobotEntity> {

    int getRewardFreezeRobotAmount(int uid,int robotLevel,String deadTime);

}

