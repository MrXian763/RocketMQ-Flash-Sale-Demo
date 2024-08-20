package com.zicai.mapper;

import com.zicai.domain.OrderRecords;
import org.apache.ibatis.annotations.Mapper;

/**
* @author xianzicai
* @description 针对表【order_records】的数据库操作Mapper
* @createDate 2024-08-20 17:11:06
* @Entity generator.domain.OrderRecords
*/
@Mapper
public interface OrderRecordsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(OrderRecords record);

    int insertSelective(OrderRecords record);

    OrderRecords selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderRecords record);

    int updateByPrimaryKey(OrderRecords record);

}
