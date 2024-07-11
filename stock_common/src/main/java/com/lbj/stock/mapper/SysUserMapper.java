package com.lbj.stock.mapper;

import com.lbj.stock.pojo.entity.SysUser;
import org.apache.ibatis.annotations.Param;

/**
* @author 95174
* @description 针对表【sys_user(用户表)】的数据库操作Mapper
* @createDate 2024-07-11 20:20:38
* @Entity com.lbj.stock.pojo.entity.SysUser
*/
public interface SysUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser findByUserName(@Param("userName") String userName);

}
