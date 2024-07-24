package com.lbj.stock.mapper;

import com.lbj.stock.pojo.domain.PermissionsTreeDomain;
import com.lbj.stock.pojo.entity.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 95174
* @description 针对表【sys_permission(权限表（菜单）)】的数据库操作Mapper
* @createDate 2024-07-11 20:20:38
* @Entity com.lbj.stock.pojo.entity.SysPermission
*/
public interface SysPermissionMapper{

    int deleteByPrimaryKey(Long id);

    int insert(SysPermission record);

    int insertSelective(SysPermission record);

    SysPermission selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysPermission record);

    int updateByPrimaryKey(SysPermission record);

    /** 根据用户id查询用户信息
     * @param userId
     * @return
     */
    List<SysPermission> getPermissionByUserId(@Param("userId") String userId);

    /**
     * 树状结构回显权限集合,底层通过递归获取权限数据集合
     * @return
     */
    List<SysPermission> getAllPermission();


    List<PermissionsTreeDomain> getAllPermissionsTree();


    int findChildrenCountByParentId(@Param("id") String id);

}
