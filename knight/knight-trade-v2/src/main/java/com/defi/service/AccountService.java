package com.defi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.defi.entity.AccountEntity;
import com.defi.entity.UserInfoEntity;
import com.defi.enums.BusinessTypeEnum;
import com.defi.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import javax.security.auth.login.AccountException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户资产记录
 *
 * @date 2021-12-24 11:09:24
 */
public interface AccountService extends IService<AccountEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addAccount(UserInfoEntity userInfoEntity);

    int addAmount(long userId, long coinId, BigDecimal amount);

    boolean addAmount(long userId,
                      long coinId,
                      BigDecimal amount,
                      BusinessTypeEnum businessTypeEnum,
                      long orderId) throws AccountException;

    int subtractAmount(long userId, long coinId, BigDecimal amount);

    boolean subtractAmount(long userId,
                           long coinId,
                           BigDecimal amount,
                           BusinessTypeEnum businessTypeEnum,
                           long orderId) throws AccountException;

    AccountEntity queryByUserIdAndCoinId(long userId, long coinId);

    List<Map> selectStaticIncomeUserList();

    int addAmountByAid(long accountId, BigDecimal amount, long coinId);

    int addSuperAmountByAid(long accountId, BigDecimal amount, long coinId);

    int addPendingAmountByAid(@Param("accountId") long accountId, @Param("amount") BigDecimal amount, @Param("coinId") long coinId);
    List<Map> selectSuperUserList();

    int selectAllAmount();
}

