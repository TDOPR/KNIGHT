package com.defi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.defi.entity.RewardFreezeRobotEntity;
import com.defi.mapper.RewardFreezeRobotDao;
import com.defi.service.RewardFreezeRobotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("rewardFreezeRobotService")
public class RewardFreezeRobotServiceImpl extends ServiceImpl<RewardFreezeRobotDao, RewardFreezeRobotEntity> implements RewardFreezeRobotService {

    @Autowired
    private RewardFreezeRobotDao rewardFreezeRobotDao;

    @Override
    public int getRewardFreezeRobotAmount(int uid, int robotLevel, String deadTime) {
        return rewardFreezeRobotDao.getRewardFreezeRobotAmount(uid,robotLevel,deadTime);
    }
}
