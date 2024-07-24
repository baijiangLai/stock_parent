package com.lbj.stock.service;

import com.lbj.stock.pojo.domain.PermissionsTreeDomain;
import com.lbj.stock.pojo.entity.SysPermission;
import com.lbj.stock.vo.AddPermissionVo;
import com.lbj.stock.vo.UpdatePermissionVo;
import com.lbj.stock.vo.resp.PermissionRespNodeVo;
import com.lbj.stock.vo.resp.R;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description
 */

public interface PermissionService {
    /**
     * 根据用户id查询用户信息
     *
     * @param userId
     * @return
     */
    List<SysPermission> getPermissionByUserId(@Param("userId") String userId);

    /**
     * @param permissions    权限树状集合
     * @param pid            权限父id，顶级权限的pid默认为0
     * @param isOnlyMenuType true:遍历到菜单，  false:遍历到按钮
     *                       type: 目录1 菜单2 按钮3
     * @return
     */
    List<PermissionRespNodeVo> getTree(List<SysPermission> permissions, String pid, boolean isOnlyMenuType);

    R<List<SysPermission>> getPermissions();

    R<List<PermissionsTreeDomain>> getPermissionsTree();

    R<String> insertPermissions(AddPermissionVo addPermissionVo);

    R<String> updatePermission(UpdatePermissionVo vo);

    R<String> deletePermission(String id);

}
