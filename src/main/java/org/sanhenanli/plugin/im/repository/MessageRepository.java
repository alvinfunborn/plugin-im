package org.sanhenanli.plugin.im.repository;

import org.sanhenanli.plugin.im.model.Message;

import java.util.List;

/**
 * datetime 2020/3/10 14:47
 * 消息读写
 *
 * @author zhouwenxiang
 */
public interface MessageRepository {

    /**
     * 保存消息
     * @param message 消息
     * @return 消息
     */
    Message save(Message message);

    /**
     * 查看会话中tag端能接收的的消息条数
     * @param imId 会话id
     * @param tag 接收端tag
     * @return 条数
     */
    int countByImIdAndTagOrTagIsNull(String imId, String tag);

    /**
     * 查看会话中msgId晚于此的tag端能接收的的消息条数
     * @param imId 会话id
     * @param msgId 消息id
     * @param tag 接收端tag
     * @return 条数
     */
    int countByImIdAndMsgIdAfterAndTagOrTagIsNull(String imId, long msgId, String tag);

    /**
     * 列出会话中所有此tag端能接收的消息
     * @param imId 会话id
     * @param tag 接收端tag
     * @param size 条数
     * @return 消息列表
     */
    List<Message> findByImIdAndTagOrTagIsNullOrderByMsgIdDesc(String imId, String tag, int size);

    /**
     * 列出会话中msgId早于此的所有此tag端能接收的消息
     * @param imId 会话id
     * @param msgId 消息id
     * @param tag 接收端tag
     * @param size 条数
     * @return 消息列表
     */
    List<Message> findByImIdAndMsgIdBeforeAndTagOrTagIsNullOrderByMsgIdDesc(String imId, long msgId, String tag, int size);

    /**
     * 列出会话中所有eid发送的消息
     * @param imId 会话id
     * @param eid 终端id
     * @param size 条数
     * @return 消息列表
     */
    List<Message> findByImIdAndEidOrderByMsgIdDesc(String imId, String eid, int size);

    /**
     * 列出会话中msgId早于此的所有eid发送的消息
     * @param imId 会话id
     * @param msgId 消息id
     * @param eid 终端id
     * @param size 条数
     * @return 消息列表
     */
    List<Message> findByImIdAndMsgIdBeforeAndEidOrderByMsgIdDesc(String imId, long msgId, String eid, int size);
}
