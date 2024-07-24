package com.lbj.stock.mapper;

import com.lbj.stock.pojo.entity.SysRolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author 95174
* @description 针对表【sys_role_permission(角色权限表)】的数据库操作Mapper
* @createDate 2024-07-11 20:20:38
* @Entity com.lbj.stock.pojo.entity.SysRolePermission
*/
public interface SysRolePermissionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysRolePermission record);

    int insertSelective(SysRolePermission record);

    SysRolePermission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysRolePermission record);

    int updateByPrimaryKey(SysRolePermission record);

    /**
     * 添加角色对应的权限
     */
    int addPermissionOfRole(@Param("id") String id, @Param("roleId") String roleId, @Param("permissionId") String permissionsId, @Param("createTime") Date createTime);

    List<String> getPerIdsOfUser(@Param("roleId") String roleId);


    int deleteByRoleId(@Param("id") String id);

    int addRolePermissionBatch(@Param("rps") List<SysRolePermission> rps);

    int deleteByPermissionId(@Param("id") String id);

}
