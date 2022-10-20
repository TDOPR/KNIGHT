package com.defi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.defi.entity.CoinConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

/**
 * eth token充值信息采集JOB表
 *
 * @date 2019-12-06 02:01:15
 */
@Mapper
public interface CoinConfigDao extends BaseMapper<CoinConfigEntity> {

    List<CoinConfigEntity> getEnableAll();

    CoinConfigEntity getEnableByToken(@Param("contract") String contract);

    CoinConfigEntity getByCoinId(@Param("coinId") Integer coinId);

    int updateActionSeqById(@Param("id") Integer id, @Param("blockNo") BigInteger blockNo);

    CoinConfigEntity getScanDataConfig();
}
