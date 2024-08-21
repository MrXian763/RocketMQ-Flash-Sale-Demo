package com.zicai.listener;

import com.zicai.service.GoodsService;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "seckillTopic",
        consumerGroup = "sckill-consumer-group",
        consumeMode = ConsumeMode.CONCURRENTLY,
        consumeThreadNumber = 40
)
public class SeckillListener implements RocketMQListener<MessageExt> {

    @Autowired
    private GoodsService goodsService;

    /**
     * 扣减库存
     * 写订单表
     *
     * @param message
     */
    @Override
    public void onMessage(MessageExt message) {
        String msg = new String(message.getBody());
        // userId + "-" + goodsId
        Integer userId = Integer.parseInt(msg.split("-")[0]);
        Integer goodsId = Integer.parseInt(msg.split("-")[1]);
        synchronized (this) { // 方案一，在事务外面加锁可以解决并发问题
            goodsService.realSeckill(userId, goodsId);
        }
    }


}
