package com.defi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.defi.entity.AccountDetailEntity;
import com.defi.enums.BusinessTypeEnum;

import java.math.BigDecimal;

/**
 * 资金账户流水
 *
 * @date 2019-11-25 10:51:13
 */
public interface AccountDetailService extends IService<AccountDetailEntity> {

    void generateBill(int userId, BusinessTypeEnum businessTypeEnum, int orderId, BigDecimal amount);

}

