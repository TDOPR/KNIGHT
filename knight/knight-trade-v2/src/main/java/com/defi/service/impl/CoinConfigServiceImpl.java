package com.defi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.defi.entity.CoinConfigEntity;
import com.defi.mapper.CoinConfigDao;
import com.defi.service.CoinConfigService;
import org.springframework.stereotype.Service;


@Service("coinConfigService")
public class CoinConfigServiceImpl extends ServiceImpl<CoinConfigDao, CoinConfigEntity> implements CoinConfigService {


}
