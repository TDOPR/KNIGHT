package com.defi.service;

import com.defi.entity.EvmEventEntity;
import com.defi.sqlPlus.CustomerService;
import com.defi.utils.PageUtils;

import java.util.Map;

/**
 * 
 *
 * @date 2021-12-24 11:09:24
 */
public interface EvmEventService extends CustomerService<EvmEventEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean getEventExistByTxHash(String txHash);

}

