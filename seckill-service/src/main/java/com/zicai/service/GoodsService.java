package com.zicai.service;


public interface GoodsService {

    /**
     * 处理秒杀业务
     * @param userId 用户 id
     * @param goodsId 商品 id
     */
    void realSeckill(Integer userId, Integer goodsId);
}
