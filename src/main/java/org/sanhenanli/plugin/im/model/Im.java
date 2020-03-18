package org.sanhenanli.plugin.im.model;

import lombok.Data;

/**
 * datetime 2020/3/10 14:51
 * 会话
 *
 * @author zhouwenxiang
 */
@Data
public class Im {

    /**
     * 会话id
     */
    protected String id;
    /**
     * 会话名称
     */
    protected String name;
    /**
     * 会话头像
     */
    protected String avatarUrl;
}
