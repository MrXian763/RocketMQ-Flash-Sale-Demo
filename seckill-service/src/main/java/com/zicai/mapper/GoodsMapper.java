package com.zicai.mapper;

import com.zicai.domain.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author xianzicai
* @description 针对表【goods】的数据库操作Mapper
* @createDate 2024-08-20 17:11:06
* @Entity generator.domain.Goods
*/
@Mapper
public interface GoodsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKey(Goods record);

    List<Goods> selectSeckillGoods();

    int updateStock(Integer goodsId);
}
