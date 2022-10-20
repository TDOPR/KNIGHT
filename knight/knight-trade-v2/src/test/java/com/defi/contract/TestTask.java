package com.defi.contract;

import com.defi.common.TaskRedisCheckKey;
import com.defi.maneger.TradeManager;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

@EnableScheduling
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class TestTask {

    @Autowired
    private TradeManager tradeManager;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @Scheduled(cron = "0/3 * * * * ?")
    public void rechargeJob() {
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

}
