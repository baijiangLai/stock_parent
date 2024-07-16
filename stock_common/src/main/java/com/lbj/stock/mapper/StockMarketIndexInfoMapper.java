package com.lbj.stock.mapper;

import com.lbj.stock.pojo.domain.InnerMarketDomain;
import com.lbj.stock.pojo.entity.StockMarketIndexInfo;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.Past;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author 95174
* @description 针对表【stock_market_index_info(国内大盘数据详情表)】的数据库操作Mapper
* @createDate 2024-07-11 20:20:38
* @Entity com.lbj.stock.pojo.entity.StockMarketIndexInfo
*/
public interface StockMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockMarketIndexInfo record);

    int insertSelective(StockMarketIndexInfo record);

    StockMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockMarketIndexInfo record);

    int updateByPrimaryKey(StockMarketIndexInfo record);

    List<InnerMarketDomain> getMarketInfo(@Param("innerCodes") List<String> innerCodes, @Param("curDate") Date curDate);

    List<Map> getSumAmtInfo(@Param("markedIds") List<String> markedIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
