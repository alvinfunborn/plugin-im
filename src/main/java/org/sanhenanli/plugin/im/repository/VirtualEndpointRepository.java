package org.sanhenanli.plugin.im.repository;

import org.sanhenanli.plugin.im.model.VirtualEndpoint;

import java.util.List;

/**
 * datetime 2020/3/11 11:12
 * 虚拟终端记录
 *
 * @author zhouwenxiang
 */
public interface VirtualEndpointRepository {

    /**
     * 记录此虚拟终端
     * @param virtualEndpoint 虚拟终端
     * @return 虚拟终端
     */
    VirtualEndpoint save(VirtualEndpoint virtualEndpoint);

    /**
     * 获取虚拟终端
     * @param imId 会话id
     * @param eid 终端id
     * @return 虚拟终端
     */
    VirtualEndpoint get(String imId, String eid);

    /**
     * 记录此虚拟终端的最后一条收到的消息
     * @param imId 会话id
     * @param eid 终端id
     * @param msgId 消息id
     * @param read true已读, false未读
     * @return 虚拟终端
     */
    VirtualEndpoint trackLastMsgId(String imId, String eid, long msgId, boolean read);

    /**
     * 列出会话中的所有虚拟终端
     * @param imId 会话id
     * @return 虚拟终端列表
     */
    List<VirtualEndpoint> findAllByImId(String imId);

    /**
     * 列出某终端的所有虚拟终端, 这些虚拟终端的最后一条接收消息早于msgId
     * @param eid 终端id
     * @param msgId 消息id
     * @param size 数量
     * @return 虚拟终端列表
     */
    List<VirtualEndpoint> findAllByEidAndMsgBeforeOrderByMsgIdDesc(String eid, String msgId, int size);
}
