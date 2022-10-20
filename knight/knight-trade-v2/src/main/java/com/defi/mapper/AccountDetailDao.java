package com.defi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.defi.entity.AccountDetailEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资金账户流水
 * 
 * @date 2019-11-25 10:51:13
 */
@Mapper
public interface AccountDetailDao extends BaseMapper<AccountDetailEntity> {

}
