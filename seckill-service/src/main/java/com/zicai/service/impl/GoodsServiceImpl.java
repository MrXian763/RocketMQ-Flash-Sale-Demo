package com.zicai.service.impl;


import com.zicai.domain.Goods;
import com.zicai.domain.OrderRecords;
import com.zicai.mapper.GoodsMapper;
import com.zicai.mapper.OrderRecordsMapper;
import com.zicai.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Autowired
    private OrderRecordsMapper orderRecordsMapper;

    /**
     * 常规的 方案
     * 锁加载调用方法的地方 要加载事务外面
     *
     * @param userId
     * @param goodsId
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // rr
    public void realSeckill(Integer userId, Integer goodsId) {
        // 扣减库存  插入订单表
        Goods goods = goodsMapper.selectByPrimaryKey(Long.valueOf(goodsId));
        int finalStock = goods.getStocks() - 1;
        if (finalStock < 0) {
            // 只是记录日志 让代码停下来   这里的异常用户无法感知
            throw new RuntimeException("库存不足：" + goodsId);
        }
        goods.setStocks(finalStock);
        goods.setUpdateTime(new Date());
        // update goods set stocks =  1 where id = 1  没有行锁
        int i = goodsMapper.updateByPrimaryKey(goods);
        if (i > 0) {
            OrderRecords order = new OrderRecords();
            order.setId(goodsId);
            order.setUserId(userId);
            order.setCreateTime(new Date());
            orderRecordsMapper.insert(order);
        }
    }
}
