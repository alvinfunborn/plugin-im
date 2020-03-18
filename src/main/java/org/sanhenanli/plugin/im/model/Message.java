package org.sanhenanli.plugin.im.model;

import lombok.Data;

/**
 * datetime 2020/3/5 15:24
 * im消息对象
 *
 * @author zhouwenxiang
 */
@Data
public class Message {

    /**
     * 消息id
     */
    protected long id;
    /**
     * 会话id
     */
    protected String imId;
    /**
     * 发送终端
     */
    protected String eid;
    /**
     * 接收终端的tag
     */
    protected String tag;
    /**
     * 消息内容: 最好是符合前后端解析协议的json字符串
     */
    protected String content;
    /**
     * 消息发送的时间戳
     */
    protected long timestamp;

}
