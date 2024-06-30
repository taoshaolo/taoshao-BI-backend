package com.taoshao.taobi.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新请求

 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;


    /**
     * 用户角色：user/admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}