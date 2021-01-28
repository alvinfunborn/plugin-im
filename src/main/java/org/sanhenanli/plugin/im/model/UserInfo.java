package org.sanhenanli.plugin.im.model;

import lombok.Data;

/**
 * datetime 2020/3/19 17:14
 *
 * @author zhouwenxiang
 */
@Data
public class UserInfo {

    /**
     * 用户id
     */
    protected String id;
    /**
     * 昵称
     */
    protected String name;
    /**
     * 头像
     */
    protected String avatarUrl;
}
