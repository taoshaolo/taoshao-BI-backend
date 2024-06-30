package com.taoshao.taobi.model.dto.user;

import com.taoshao.taobi.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求

 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;


    /**
     * 用户角色：user/admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}