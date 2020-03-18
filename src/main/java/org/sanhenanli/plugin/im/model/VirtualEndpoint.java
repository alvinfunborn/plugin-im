package org.sanhenanli.plugin.im.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * datetime 2020/3/11 10:51
 * 虚拟终端及其状态: 指个人在不同会话中的不同终端对象, 同时记录了此终端的最后一条已读消息和未读消息
 *
 * @author zhouwenxiang
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class VirtualEndpoint extends Endpoint {

    /**
     * 虚拟终端id
     */
    protected String id;
    /**
     * 接收器tag
     */
    protected String tag;
    /**
     * 会话id
     */
    protected String imId;
    /**
     * 最近一条消息的id
     */
    protected Long lastMsgId;
    /**
     * 最后一条已读消息的id
     */
    protected Long lastReadMsgId;

    public VirtualEndpoint(Endpoint endpoint) {
        this.eid = endpoint.getEid();
    }
}
