package com.defi.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.defi.sqlPlus.CustomerSqlInjector;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis配置
 *
 * @author chqiu
 */
@Configuration(proxyBeanMethods = false)
@MapperScan("com.defi.mapper")
public class MybatisConfig {
    /**
     * 3.4.0 以后的配置方式
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 分页配置
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 自定义sql注入器
     * 关键部位重要的事情说三遍，不注入不生效，不注入不生效，不注入不生效
     */
    @Bean
    public ISqlInjector iSqlInjector() {
        return new CustomerSqlInjector();
    }
}
