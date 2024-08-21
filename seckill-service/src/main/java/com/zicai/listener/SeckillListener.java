package com.zicai.listener;

import com.zicai.service.GoodsService;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RocketMQMessageListener(topic = "seckillTopic",
        consumerGroup = "sckill-consumer-group",
        consumeMode = ConsumeMode.CONCURRENTLY,
        consumeThreadNumber = 40
)
public class SeckillListener implements RocketMQListener<MessageExt> {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 锁的过期时间 50s
    private static final int ZX_TIME = 50000;

    /**
     * 扣减库存
     * 写订单表
     *
     * @param message
     */
//    @Override
//    public void onMessage(MessageExt message) {
//        String msg = new String(message.getBody());
//        // userId + "-" + goodsId
//        Integer userId = Integer.parseInt(msg.split("-")[0]);
//        Integer goodsId = Integer.parseInt(msg.split("-")[1]);
//        synchronized (this) { // 方案一，在事务外面加锁可以解决并发问题
//            goodsService.realSeckill(userId, goodsId);
//        }
//    }

    /**
     * 方案二 分布式锁 MySQL行锁 Redis锁
     * @param message
     */
//    @Override
//    public void onMessage(MessageExt message) {
//        String msg = new String(message.getBody());
//        // userId + "-" + goodsId
//        Integer userId = Integer.parseInt(msg.split("-")[0]);
//        Integer goodsId = Integer.parseInt(msg.split("-")[1]);
//        goodsService.realSeckill(userId, goodsId);
//    }

    /**
     * 方案三 redis setnx 分布式锁
     * 对比与上面的 MySQL 行锁，这种方式优点：压力分摊到 redis 和程序中，缓解 db 压力
     *
     * @param message
     */
    @Override
    public void onMessage(MessageExt message) {
        String msg = new String(message.getBody());
        // userId + "-" + goodsId
        Integer userId = Integer.parseInt(msg.split("-")[0]);
        Integer goodsId = Integer.parseInt(msg.split("-")[1]);

        // 锁 ＋ 自旋时间
        int currentThreadTime = 0;
        while (currentThreadTime < ZX_TIME) {
            // 设置过期时间，避免死锁
            Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent("lock:" + goodsId, "", Duration.ofSeconds(30));
            if (flag) {
                // 抢到锁
                try {
                    // 执行业务
                    goodsService.realSeckill(userId, goodsId);
                    break; // 跳出循环
                } finally {
                    // 释放锁
                    stringRedisTemplate.delete("lock:" + goodsId);
                }
            } else {
                // 没抢到锁 拿锁尝试 10000 / 200 = 50 次
                currentThreadTime += 200;
                // 睡眠 200ms 再尝试抢锁
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
