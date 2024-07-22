package com.lbj.stock.shareding;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description 定义公共的数据库分片算法类：包含精准匹配数据库和范围匹配数据库
 *  因为分库是根据日期分库的，一年一个库，所以片键的类型是Date
 *  覆盖个股、大盘、板块相关表
 */
public class CommonShardingAlgorithm4Db implements PreciseShardingAlgorithm<Date>, RangeShardingAlgorithm<Date> {

    /**
     * 精准匹配数据库的方法 cur_time 条件必须是 = 或者in
     * @param dsNames 所有可匹配数据源的集合 ds-2021 ds-2022
     * @param shardingValue
     * @return
     */
    @Override
    public String doSharding(Collection<String> dsNames, PreciseShardingValue<Date> shardingValue) {
        //1.思路：根据传入的日期值，获取年份字符串
        //获取分片字段的名称column-> cur_time
        String columnName = shardingValue.getColumnName();
        //获取逻辑表名称
        String logicTableName = shardingValue.getLogicTableName();
        //获取分片值
        Date value = shardingValue.getValue();
        //获取年份字符串
        String year = new DateTime(value).getYear()+"";
        //2.获取数据源中以
        Optional<String> optional = dsNames.stream().filter(ds -> ds.endsWith(year)).findFirst();
        String actual=null;
        //判断是否有符合指定年份的数据源
        if (optional.isPresent()) {
            actual=optional.get();
        }
        return actual;
    }

    /**
     * 范围查询匹配数据源 关键字：between and
     * @param dsNames ds-2021 ds-2022
     * @param shardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> dsNames, RangeShardingValue<Date> shardingValue) {
        //获取分片字段名称
        String columnName = shardingValue.getColumnName();
        //获取逻辑表名称
        String logicTableName = shardingValue.getLogicTableName();
        //1.获取范围封装对象
        Range<Date> valueRange = shardingValue.getValueRange();
        //2.1 判断是否有下限值
        if (valueRange.hasLowerBound()) {
            //获取下限日期
            Date lowerDate = valueRange.lowerEndpoint();
            //获取年份  dsNames--> ds_2021 ds_2022 ds_2023
            int startYear = new DateTime(lowerDate).getYear();//2022
            dsNames= dsNames.stream().filter(dsName->Integer.valueOf(dsName.substring(dsName.lastIndexOf("-")+1))>=startYear)
                    .collect(Collectors.toList());
        }
        //2.2 判断是否有上限值
        if (valueRange.hasUpperBound()) {
            Date upperDate = valueRange.upperEndpoint();
            int endYear = new DateTime(upperDate).getYear();
            dsNames= dsNames.stream().filter(dsName->Integer.valueOf(dsName.substring(dsName.lastIndexOf("-")+1))<=endYear)
                    .collect(Collectors.toList());
        }

        return dsNames;
    }
}
