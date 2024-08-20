package com.zicai.controller;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeckillController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 秒杀接口
     * 1.用户去重
     * 2.库存预扣减
     * 3.消息放入MQ
     *
     * @param goodsId 商品id
     * @param userId  用户id（实际可以在登录信息中获取）
     * @return
     */
    @GetMapping("seckill")
    public String doSecKill(Integer goodsId, Integer userId) {
        // 1.用户去重
        // uk uniqueKey = [yyyyMMdd] + userId + goodsId
        String uk = userId + "-" + goodsId;
        // setIfAbsent = setnx
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(uk, "");
        if (!flag) {
            return "你已经参与过该商品的抢购！";
        }

        // TODO 2.库存预扣减（先减少库存，再判断，线程安全）
        Long count = stringRedisTemplate.opsForValue().decrement("goodsId:" + goodsId);
        if (count < 0) {
            return "商品库存不足，请下次再来";
        }

        // 3.消息放入MQ 异步处理
        rocketMQTemplate.asyncSend("seckillTopic", uk, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("消息发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("消息发送失败，用户id：" + userId + "，商品id：" + goodsId);
                throwable.printStackTrace();
            }
        });
        return "正在为您抢购中，请稍后前往订单中心查看结果";
    }
}
