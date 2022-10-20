package com.defi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.defi.entity.AccountDetailEntity;
import com.defi.enums.BusinessTypeEnum;
import com.defi.enums.CoinEnum;
import com.defi.mapper.AccountDetailDao;
import com.defi.service.AccountDetailService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;


@Service("accountDetailService")
public class AccountDetailServiceImpl extends ServiceImpl<AccountDetailDao, AccountDetailEntity> implements AccountDetailService {

    @Override
    public void generateBill(int userId, BusinessTypeEnum businessTypeEnum, int orderId, BigDecimal amount) {
        //流水记录
        AccountDetailEntity accountDetailEntity = new AccountDetailEntity();
        //默认链上账单，accountId为99,coin目前默认都收U
        accountDetailEntity.setAccountId(Integer.parseInt("99"));
        accountDetailEntity.setBusinessType(businessTypeEnum.getCode());
        accountDetailEntity.setUid(Integer.parseInt(userId + ""));
        accountDetailEntity.setCreated(new Date());
        accountDetailEntity.setCoinId((int) CoinEnum.RECHARGE_USDT.getCoinId());
        accountDetailEntity.setRemark(businessTypeEnum.getDesc());
        accountDetailEntity.setOrderId(orderId);
        accountDetailEntity.setDirection(2);
        accountDetailEntity.setRefAccountId(99);
        accountDetailEntity.setAmount(amount);
        baseMapper.insert(accountDetailEntity);
    }
}
