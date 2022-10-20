package com.defi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.defi.entity.TreePathsEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @date 2019-12-07 11:14:49
 */
public interface TreePathsService extends IService<TreePathsEntity> {
    List<Map> getNumByLevel(int uid);
    Map getLevelById(int uid);
}

