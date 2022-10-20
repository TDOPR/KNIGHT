package com.defi.mapper;

import com.defi.entity.EvmEventEntity;
import com.defi.sqlPlus.CustomerMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * 
 * @date 2021-12-24 11:09:24
 */
@Mapper
public interface EvmEventDao extends CustomerMapper<EvmEventEntity> {

	int getCountByTxHash(@Param("txHash") String txHash);

}
