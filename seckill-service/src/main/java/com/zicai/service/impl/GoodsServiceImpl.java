package com.zicai.service.impl;


import com.zicai.mapper.GoodsMapper;
import com.zicai.mapper.OrderRecordsMapper;
import com.zicai.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Autowired
    private OrderRecordsMapper orderRecordsMapper;

}
