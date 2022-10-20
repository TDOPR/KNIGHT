package com.defi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.defi.entity.TreePathsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @date 2019-12-07 11:14:49
 */
@Mapper
public interface TreePathsDao extends BaseMapper<TreePathsEntity> {

    List<Map> getNumByLevel(@Param("uid") int uid);
    Map getLevelById(@Param("uid") int uid);
}
