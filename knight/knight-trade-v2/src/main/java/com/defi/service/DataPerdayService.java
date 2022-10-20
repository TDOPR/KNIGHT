package com.defi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.defi.entity.AccountEntity;
import com.defi.entity.DataPerdayEntity;
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
public interface DataPerdayService extends IService<DataPerdayEntity> {
    void addAmount(BigDecimal amount);
    void substractAmount(BigDecimal amount);

    int selectallamount();

}

