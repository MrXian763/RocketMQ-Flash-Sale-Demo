package com.zicai.service.impl;

import com.zicai.mapper.OrderRecordsMapper;
import com.zicai.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderRecordsMapper orderRecordsMapper;

}
