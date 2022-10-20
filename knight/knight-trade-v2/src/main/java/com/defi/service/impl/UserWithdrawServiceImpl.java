package com.defi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.defi.entity.UserOrderEntity;
import com.defi.entity.UserWithdrawEntity;
import com.defi.mapper.UserOrderDao;
import com.defi.mapper.UserWithdrawDao;
import com.defi.service.UserOrderService;
import com.defi.service.UserWithdrawService;
import org.springframework.stereotype.Service;

@Service("UserWithdrawService")
public class UserWithdrawServiceImpl extends ServiceImpl<UserWithdrawDao, UserWithdrawEntity> implements UserWithdrawService {

}
