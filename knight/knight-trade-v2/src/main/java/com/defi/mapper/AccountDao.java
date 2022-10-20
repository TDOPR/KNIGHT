package com.defi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.defi.entity.AccountEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户资产记录
 * 
 * @date 2021-12-24 11:09:24
 */
@Mapper
public interface AccountDao extends BaseMapper<AccountEntity> {

    int addAmountByAid(@Param("accountId") long accountId, @Param("amount") BigDecimal amount, @Param("coinId") long coinId);

    int addSuperAmountByAid(@Param("accountId") long accountId, @Param("amount") BigDecimal amount, @Param("coinId") long coinId);

    int subtractAmount(@Param("userId") long userId, @Param("coinId") long coinId, @Param("amount") BigDecimal amount);

    int subtractAmountByAid(@Param("accountId") long accountId, @Param("amount") BigDecimal amount);

    List<Map> selectStaticIncomeUserList();

    int addPendingAmountByAid(@Param("accountId") long accountId, @Param("amount") BigDecimal amount, @Param("coinId") long coinId);

    List<Map> selectSuperUserList();

    int selectAllAmount();
}
