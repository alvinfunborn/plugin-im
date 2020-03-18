package org.sanhenanli.plugin.im.model;

import lombok.Data;

/**
 * datetime 2020/3/5 15:30
 * 消息盒子: 指一个会话的最后一条消息和未读条数等
 *
 * @author zhouwenxiang
 */
@Data
public class MessageBox {

    /**
     * 消息id
     */
    protected long messageId;
    /**
     * 会话id
     */
    protected String imId;
    /**
     * 最后一条消息内容
     */
    protected String content;
    /**
     * 发送终端id
     */
    protected String eid;
    /**
     * 消息时间戳
     */
    protected long timestamp;
    /**
     * 未读条数
     */
    protected int unread;

}
