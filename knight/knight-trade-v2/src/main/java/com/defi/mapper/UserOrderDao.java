package com.defi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.defi.entity.UserOrderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserOrderDao extends BaseMapper<UserOrderEntity> {

}
