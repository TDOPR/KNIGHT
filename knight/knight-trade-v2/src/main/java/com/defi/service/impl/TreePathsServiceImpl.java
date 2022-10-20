package com.defi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.defi.entity.TreePathsEntity;
import com.defi.mapper.TreePathsDao;
import com.defi.service.TreePathsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("treePathsService")
public class TreePathsServiceImpl extends ServiceImpl<TreePathsDao, TreePathsEntity> implements TreePathsService {

    @Autowired
    private TreePathsDao treePathsDao;

    @Override
    public List<Map> getNumByLevel(int uid) {
        return treePathsDao.getNumByLevel(uid);
    }

    @Override
    public Map getLevelById(int uid) {
        return treePathsDao.getLevelById(uid);
    }

}
