package com.atguigu.service.impl;

import com.atguigu.entity.UserInfo;
import com.atguigu.mapper.UserInfoMapper;
import com.atguigu.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-10-30
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Override
    public UserInfo queryUserFromDb(UserInfo uiUserInfo) {
        //查询字段均为非主键，用Wrapper查询合适
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("login_name",uiUserInfo.getLoginName());
        //对页面传递过来的密码进行加密
        String passwd = uiUserInfo.getPasswd();
        String encodePasswd = DigestUtils.md5DigestAsHex(passwd.getBytes());
        userInfoQueryWrapper.eq("passwd",encodePasswd);
        return baseMapper.selectOne(userInfoQueryWrapper);
    }
}
