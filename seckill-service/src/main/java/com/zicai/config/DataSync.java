package com.zicai.config;

import com.zicai.domain.Goods;
import com.zicai.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 将数据库中的商品库存信息同步到数据库
 * 可以设定一个定时任务，定时同步数据库中的库存信息
 * 为了方便演示，只执行一次
 * bean 生命周期：实例化 new -> 属性赋值 -> 初始化 init -> 使用 -> 销毁 destroy
 */
@Component
public class DataSync {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 方法在项目启动后，等待类的属性都被注入后，执行一次
     */
    @PostConstruct
    public void initData() {
        List<Goods> goodsList = goodsMapper.selectSeckillGoods();
        if (CollectionUtils.isEmpty(goodsList)) {
            return;
        }
        goodsList.forEach(goods -> {
            stringRedisTemplate.opsForValue().set("goodsId:" + goods.getId(), goods.getStocks().toString());
        });
    }
}
