package com.defi.job;

import com.defi.common.TaskRedisCheckKey;
import com.defi.condition.ETHEnableCondition;
import com.defi.maneger.EventManager;
import com.defi.maneger.TradeManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 批量生成以太坊地址放入地址池
 */
@Component
@Slf4j
@EnableScheduling
@Conditional(ETHEnableCondition.class)
public class TradeManagerJob {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TradeManager tradeManager;

    @Autowired
    private EventManager eventManager;

    @Scheduled(cron = "0/3 * * * * ?")
    public void analyzeEvent() {
        try {
            boolean bool = redisTemplate.opsForValue().setIfAbsent(TaskRedisCheckKey.ANALYZE_EVENT, "", Duration.ofSeconds(50));
            if (bool) {
                eventManager.analyzeEvent();
                redisTemplate.delete(TaskRedisCheckKey.ANALYZE_EVENT);
            } else {
                log.error(log.getName() + "同步事务失败，上一任务正在运行!");
            }
        } catch (Exception e) {
            log.error("同步事务失败,失败信息【{}】", e);
            redisTemplate.delete(TaskRedisCheckKey.ANALYZE_EVENT);
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0/3 * * * * ?")
    public void staticIncome() {
        try {
            boolean bool = redisTemplate.opsForValue().setIfAbsent(TaskRedisCheckKey.STATIC_INCOME, "", Duration.ofSeconds(50));
            if (bool) {
                tradeManager.staticIncome();
                redisTemplate.delete(TaskRedisCheckKey.STATIC_INCOME);
            } else {
                log.error(log.getName() + "发放静态收益失败，上一任务正在运行!");
            }
        } catch (Exception e) {
            log.error("发放静态收益失败,失败信息【{}】", e);
            redisTemplate.delete(TaskRedisCheckKey.STATIC_INCOME);
            e.printStackTrace();
        }
    }

    //        @Scheduled(cron = "0/1 * * * * ?")
//    @Scheduled(cron = "0 0 1 * * ?")
//    public void powerIncome() {
//        log.info("=============发放静态收益开始===============");
//        try {
//            boolean bool = redisTemplate.opsForValue().setIfAbsent(TaskRedisCheckKey.POWER_INCOME, "", Duration.ofSeconds(300));
//            if (bool) {
//                ahtManager.powerIncome();
//                redisTemplate.delete(TaskRedisCheckKey.POWER_INCOME);
//            } else {
//                log.error(log.getName() + "powerIncome失败，上一任务正在运行!");
//            }
//        } catch (Exception e) {
//            log.error("powerIncome失败,失败信息【{}】", e);
//            redisTemplate.delete(TaskRedisCheckKey.POWER_INCOME);
//            e.printStackTrace();
//        }
//        log.info("==============发放静态收益结束===============");
//    }

}
