package com.defi.sqlPlus;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * 此处可以写通用的Service
 *
 * @param <M>
 * @param <T>
 * @author chqiu
 */
public class CustomerServiceImpl<M extends CustomerMapper<T>, T> extends ServiceImpl<CustomerMapper<T>, T> implements CustomerService<T> {

    @Override
    public int insertIgnore(T entity) {
        return baseMapper.insertIgnore(entity);
    }

    @Override
    public int insertIgnoreBatch(List<T> entityList) {
        return baseMapper.insertIgnoreBatch(entityList);
    }

    @Override
    public int replace(T entity) {
        return baseMapper.replace(entity);
    }
}
