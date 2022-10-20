package com.defi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.defi.entity.AccountDetailEntity;
import com.defi.entity.AccountEntity;
import com.defi.entity.DataPerdayEntity;
import com.defi.entity.UserInfoEntity;
import com.defi.enums.BusinessTypeEnum;
import com.defi.exception.AccountException;
import com.defi.mapper.AccountDao;
import com.defi.mapper.AccountDetailDao;
import com.defi.mapper.DataPerdayDao;
import com.defi.service.AccountService;
import com.defi.service.DataPerdayService;
import com.defi.utils.Help;
import com.defi.utils.PageUtils;
import com.defi.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Service("dataPerdayService")
public class DataPerdayServiceImpl extends ServiceImpl<DataPerdayDao, DataPerdayEntity> implements DataPerdayService {
    @Override
    public void addAmount(BigDecimal amount){
        baseMapper.addAmount(amount);
    }

    @Override
    public void substractAmount(BigDecimal amount){
        baseMapper.subtractAmount(amount);
    }

    @Override
    public int selectallamount(){
        return baseMapper.selectallamount();
    }

}
