package com.defi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.defi.entity.RewardFreezeRobotEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface RewardFreezeRobotDao extends BaseMapper<RewardFreezeRobotEntity> {

    int getRewardFreezeRobotAmount(@Param("uid") int uid, @Param("robotLevel") int robotLevel, @Param("deadTime") String deadTime);
}
