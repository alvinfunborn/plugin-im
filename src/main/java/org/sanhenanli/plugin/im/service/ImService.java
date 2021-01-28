package org.sanhenanli.plugin.im.service;

import org.sanhenanli.plugin.im.model.*;

import java.util.List;
import java.util.Map;

/**
 * datetime 2020/3/10 13:47
 * 会话服务
 *
 * @author zhouwenxiang
 */
public interface ImService {

    /**
     * 根据唯一票据获取会话id
     * @param ticket 票据
     * @return 会话id
     */
    String getImId(String ticket);

    /**
     * 根据唯一用户id获取终端id
     * @param id 用户id
     * @return 终端id
     */
    String getEid(String id);

    /**
     * 根据唯一票据和用户id和用户tag获取虚拟终端
     * @param ticket 票据
     * @param id 用户id
     * @param tag 用户tag, 用于接收会话中的点对点消息
     * @return 虚拟终端
     */
    VirtualEndpoint getVirtualEndpoint(String ticket, String id, String tag);

    /**
     * 发送消息
     * @param message 消息
     * @return 发送结果<eid, true/false> true指发送给此eid终端成功
     */
    Map<String, Boolean> send(Message message);

    /**
     * 获取某会话某终端的消息盒子
     * @param imId 会话id
     * @param eid 终端id
     * @return 消息盒子
     */
    MessageBox box(String imId, String eid);

    /**
     * 获取某终端的消息盒子列表, 这些消息盒子的最后一条消息早于此msgId
     * @param eid 终端id
     * @param msgId 消息id
     * @param size 数量
     * @return 消息盒子
     */
    List<MessageBox> historyBox(String eid, String msgId, int size);

    /**
     * 获取会话中某终端的消息记录, 这些消息早于此msgId
     * @param imId 会话id
     * @param eid 终端id
     * @param msgId 消息id
     * @param size 条数
     * @return 消息列表
     */
    List<Message> history(String imId, String eid, String msgId, int size);

    /**
     * 获取会话中某终端的发送历史, 这些消息早于此msgId
     * @param imId 会话id
     * @param eid 终端id
     * @param msgId 消息id
     * @param size 条数
     * @return 消息列表
     */
    List<Message> historySent(String imId, String eid, String msgId, int size);
}
