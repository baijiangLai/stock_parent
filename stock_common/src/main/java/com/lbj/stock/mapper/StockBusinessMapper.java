package com.lbj.stock.mapper;

import com.lbj.stock.pojo.domain.StockRtDescribeDomain;
import com.lbj.stock.pojo.entity.StockBusiness;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 95174
* @description 针对表【stock_business(主营业务表)】的数据库操作Mapper
* @createDate 2024-07-11 20:20:38
* @Entity com.lbj.stock.pojo.entity.StockBusiness
*/
public interface StockBusinessMapper {

    int deleteByPrimaryKey(String id);

    int insert(StockBusiness record);

    int insertSelective(StockBusiness record);

    StockBusiness selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockBusiness record);

    int updateByPrimaryKey(StockBusiness record);

    List<String> getStockIds();

    StockRtDescribeDomain getStockRtDescribe(@Param("code") String code);
}
