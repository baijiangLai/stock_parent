package com.lbj.stock.mapper;

import com.lbj.stock.pojo.domain.StockUpdownDomain;
import com.lbj.stock.pojo.entity.StockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author 95174
* @description 针对表【stock_rt_info(个股详情信息表)】的数据库操作Mapper
* @createDate 2024-07-11 20:20:38
* @Entity com.lbj.stock.pojo.entity.StockRtInfo
*/
public interface StockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockRtInfo record);

    int insertSelective(StockRtInfo record);

    StockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockRtInfo record);

    int updateByPrimaryKey(StockRtInfo record);

    List<StockUpdownDomain> getNewestStockInfo(@Param("curDate") Date curDate);

    List<StockUpdownDomain> getIncreaseTop4(@Param("curDate") Date curDate);

    List<Map> getStockUpDownCount(@Param("openTime") Date openTime, @Param("curTime") Date curTime, @Param("flag") int flag);

}
