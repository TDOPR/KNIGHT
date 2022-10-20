package com.defi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.defi.entity.UserOrderEntity;
import com.defi.mapper.UserOrderDao;
import com.defi.service.UserOrderService;
import org.springframework.stereotype.Service;

@Service("userOrderService")
public class UserOrderServiceImpl extends ServiceImpl<UserOrderDao, UserOrderEntity> implements UserOrderService {


}
