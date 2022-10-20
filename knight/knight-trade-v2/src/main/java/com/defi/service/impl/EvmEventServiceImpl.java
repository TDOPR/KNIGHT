package com.defi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.defi.entity.EvmEventEntity;
import com.defi.mapper.EvmEventDao;
import com.defi.service.EvmEventService;
import com.defi.sqlPlus.CustomerServiceImpl;
import com.defi.utils.PageUtils;
import com.defi.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("evmEventService")
public class EvmEventServiceImpl extends CustomerServiceImpl<EvmEventDao, EvmEventEntity> implements EvmEventService {

    @Autowired
    private EvmEventDao evmEventDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EvmEventEntity> page = this.page(
                new Query<EvmEventEntity>().getPage(params),
                new QueryWrapper<EvmEventEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean getEventExistByTxHash(String txHash) {
        int num = evmEventDao.getCountByTxHash(txHash);
        return num > 0;
    }
}
