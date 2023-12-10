package com.atguigu.service;

import com.atguigu.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-10-30
 */
public interface UserInfoService extends IService<UserInfo> {

    UserInfo queryUserFromDb(UserInfo uiUserInfo);

}
