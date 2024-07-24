package com.lbj.stock.service.impl;

import com.google.common.collect.Lists;
import com.lbj.stock.enums.MagicValue;
import com.lbj.stock.exception.BusinessException;
import com.lbj.stock.mapper.SysPermissionMapper;
import com.lbj.stock.mapper.SysRolePermissionMapper;
import com.lbj.stock.pojo.domain.PermissionsTreeDomain;
import com.lbj.stock.pojo.entity.SysPermission;
import com.lbj.stock.service.PermissionService;
import com.lbj.stock.utils.IdWorker;
import com.lbj.stock.vo.AddPermissionVo;
import com.lbj.stock.vo.UpdatePermissionVo;
import com.lbj.stock.vo.resp.PermissionRespNodeVo;
import com.lbj.stock.vo.resp.R;
import com.lbj.stock.vo.resp.ResponseCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description
 */

@Service("permissionService")
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    /**
     * @param permissions    权限树状集合
     * @param pid            权限父id，顶级权限的pid默认为0
     * @param isOnlyMenuType true:遍历到菜单，  false:遍历到按钮
     *                       type: 目录1 菜单2 按钮3
     * @return
     */
    @Override
    public List<PermissionRespNodeVo> getTree(List<SysPermission> permissions, String pid, boolean isOnlyMenuType) {
        ArrayList<PermissionRespNodeVo> list = Lists.newArrayList();
        if (CollectionUtils.isEmpty(permissions)) {
            return list;
        }
        for (SysPermission permission : permissions) {
            if (permission.getPid().equals(pid)) {
                if (permission.getType().intValue() != 3 || !isOnlyMenuType) {
                    PermissionRespNodeVo respNodeVo = new PermissionRespNodeVo();
                    respNodeVo.setId(String.valueOf(permission.getId()));
                    respNodeVo.setTitle(permission.getTitle());
                    respNodeVo.setIcon(permission.getIcon());
                    respNodeVo.setPath(permission.getUrl());
                    respNodeVo.setName(permission.getName());
                    respNodeVo.setChildren(getTree(permissions, String.valueOf(permission.getId()), isOnlyMenuType));
                    list.add(respNodeVo);
                }
            }
        }
        return list;
    }

    @Override
    public R<List<SysPermission>> getPermissions() {
        List<SysPermission> info = sysPermissionMapper.getAllPermission();
//        System.out.println(info);
        return R.ok(info);
    }

    @Override
    public R<List<PermissionsTreeDomain>> getPermissionsTree() {
        List<PermissionsTreeDomain> infos = sysPermissionMapper.getAllPermissionsTree();
        return R.ok(infos);
    }

    @Override
    public R<String> insertPermissions(AddPermissionVo addPermissionVo) {
        SysPermission sysPermission = new SysPermission();
        BeanUtils.copyProperties(addPermissionVo, sysPermission);
        this.checkPermissionForm(sysPermission);
        //1.组装权限对象
        sysPermission.setStatus(MagicValue.STATUS_NORMAL);
        sysPermission.setCreateTime(new Date());
        sysPermission.setUpdateTime(new Date());
        sysPermission.setDeleted(MagicValue.STATUS_NOT_DEL);
        sysPermission.setId(Long.valueOf(idWorker.nextId()+""));
//        sysPermission = sysPermission.builder()
//                .status(MagicValue.STATUS_NORMAL)
//                .createTime(new Date())
//                .updateTime(new Date())
//                .deleted(MagicValue.STATUS_NOT_DEL)
//                .id(idWorker.nextId() + "")
//                .build();
        int count = sysPermissionMapper.insert(sysPermission);
        if (count != MagicValue.SUCCESSVAL) {
            return R.error(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    @Override
    public R<String> updatePermission(UpdatePermissionVo vo) {
        SysPermission sysPermission = new SysPermission();
        BeanUtils.copyProperties(vo, sysPermission);
        this.checkPermissionForm(sysPermission);

        sysPermission.setStatus(MagicValue.STATUS_NORMAL);
        sysPermission.setUpdateTime(new Date());
        sysPermission.setDeleted(MagicValue.STATUS_NOT_DEL);

//        sysPermission = sysPermission.builder()
//                .status(MagicValue.STATUS_NORMAL)
//                .updateTime(new Date())
//                .deleted(MagicValue.STATUS_NOT_DEL).build();

        int count = sysPermissionMapper.updateByPrimaryKey(sysPermission);
        if (count != MagicValue.SUCCESSVAL) {
            return R.error(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    @Override
    public R<String> deletePermission(String id) {
        //1.判断当前角色是否有角色自己，有则不能删除
        int count = this.sysPermissionMapper.findChildrenCountByParentId(id);
        if (count > 0) {
            return R.error(ResponseCode.ROLE_PERMISSION_RELATION.getMessage());
        }
        //2.删除角色关联权限的信息
        sysRolePermissionMapper.deleteByPermissionId(id);
        //3.更新权限状态为已删除
        SysPermission permission = SysPermission.builder().id(Long.valueOf(id)).deleted(MagicValue.STATUS_DEL).updateTime(new Date()).build();
        int updateCount = this.sysPermissionMapper.updateByPrimaryKeySelective(permission);
        if (updateCount != MagicValue.SUCCESSVAL) {
            return R.error(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());

    }

    /**
     * 根据用户id查询权限集合
     *
     * @param userId
     * @return
     */
    @Override
    public List<SysPermission> getPermissionByUserId(String userId) {
        return sysPermissionMapper.getPermissionByUserId(userId);
    }
    //......

    /**
     * 检查添加或者更新的权限提交表单是否合法，如果不合法，则直接抛出异常
     * 检查规则：目录的父目录等级必须为0或者其他目录（等级为1）
     * 菜单的父父级必须是1，也就是必须是父目录，
     * 按钮的父级必须是菜单，也是是等级是3，且父级是2
     * 其他关联的辨识 url等信息也可做相关检查
     *
     * @param vo
     */
    private void checkPermissionForm(SysPermission vo) {
        if (vo != null || vo.getType() != null || vo.getPid() != null) {
            //获取权限类型 0：顶级目录 1.普通目录 2.菜单 3.按钮
            Integer type = vo.getType();
            //获取父级id
            String pid = String.valueOf(vo.getPid());
            //根据父级id查询父级信息
            SysPermission parentPermission = sysPermissionMapper.selectByPrimaryKey(pid);
            if (type == 1) {
                if (!pid.equals("0") || (parentPermission != null && parentPermission.getType() > 1)) {
                    throw new BusinessException(ResponseCode.OPERATION_MENU_PERMISSION_CATALOG_ERROR.getMessage());
                }
            } else if (type == 2) {
                if (parentPermission == null || parentPermission.getType() != 1) {
                    throw new BusinessException(ResponseCode.OPERATION_MENU_PERMISSION_CATALOG_ERROR.getMessage());
                }
                if (StringUtils.isBlank(vo.getUrl())) {
                    throw new BusinessException(ResponseCode.OPERATION_MENU_PERMISSION_URL_CODE_NULL.getMessage());
                }
            } else if (type == 3) {
                if (parentPermission == null || parentPermission.getType() != 2) {
                    throw new BusinessException(ResponseCode.OPERATION_MENU_PERMISSION_BTN_ERROR.getMessage());
                } else if (vo.getUrl() == null || vo.getCode() == null || vo.getMethod() == null) {
                    throw new BusinessException(ResponseCode.DATA_ERROR.getMessage());
                }
            } else {
                throw new BusinessException(ResponseCode.DATA_ERROR.getMessage());
            }
        } else {
            throw new BusinessException(ResponseCode.DATA_ERROR.getMessage());
        }
    }
}
