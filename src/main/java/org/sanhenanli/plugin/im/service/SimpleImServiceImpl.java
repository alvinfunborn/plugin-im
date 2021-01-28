package org.sanhenanli.plugin.im.service;

import com.alibaba.fastjson.JSONObject;
import org.sanhenanli.plugin.im.model.*;
import org.sanhenanli.plugin.im.repository.EndpointFocusRepository;
import org.sanhenanli.plugin.im.repository.MessageRepository;
import org.sanhenanli.plugin.im.repository.VirtualEndpointRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * datetime 2020/3/10 14:49
 *
 * @author zhouwenxiang
 */
public class SimpleImServiceImpl implements ImService {

    private MessageRepository messageRepository;
    private VirtualEndpointRepository virtualEndpointRepository;
    private EndpointFocusRepository endpointFocusRepository;
    private ImPusher imPusher;

    public SimpleImServiceImpl(MessageRepository messageRepository, VirtualEndpointRepository virtualEndpointRepository, EndpointFocusRepository endpointFocusRepository, ImPusher imPusher) {
        this.messageRepository = messageRepository;
        this.virtualEndpointRepository = virtualEndpointRepository;
        this.endpointFocusRepository = endpointFocusRepository;
        this.imPusher = imPusher;
    }

    @Override
    public Map<String, Boolean> send(Message message) {
        Map<String, Boolean> sendResult = new HashMap<>(4);
        messageRepository.save(message);
        String imId = message.getImId();
        List<VirtualEndpoint> virtualEndpoints = virtualEndpointRepository.findAllByImId(imId);
        virtualEndpoints.forEach(ve -> {
            if (message.getTag() == null || ve.getTag().equals(message.getTag())) {
                boolean pushed = imPusher.push(ve.getEid(), JSONObject.toJSONString(message));
                if (pushed) {
                    // 成功推送, 终端在连接状态, 判断是已读未读
                    String currentImId = endpointFocusRepository.currentIm(ve.getEid());
                    if (message.getImId().equals(currentImId)) {
                        // 消息已读
                        virtualEndpointRepository.trackLastMsgId(message.getImId(), ve.getEid(), message.getId(), true);
                    } else {
                        // 消息未读
                        virtualEndpointRepository.trackLastMsgId(message.getImId(), ve.getEid(), message.getId(), false);
                    }
                    sendResult.put(ve.getEid(), true);
                } else {
                    // 推送失败, 消息未读
                    virtualEndpointRepository.trackLastMsgId(message.getImId(), ve.getEid(), message.getId(), false);
                    sendResult.put(ve.getEid(), false);
                }
            }
        });
        return sendResult;
    }

    @Override
    public String getImId(String ticket) {
        return ticket;
    }

    @Override
    public String getEid(String id) {
        return id;
    }

    @Override
    public VirtualEndpoint getVirtualEndpoint(String ticket, String id, String tag) {
        // 进入会话框前注册虚拟终端
        String imId = getImId(ticket);
        String eid = getEid(id);
        Endpoint endpoint = new Endpoint();
        endpoint.setEid(eid);
        VirtualEndpoint virtualEndpoint = getVirtualEndpoint(imId, eid);
        if (virtualEndpoint == null) {
            virtualEndpoint = new VirtualEndpoint(endpoint);
            virtualEndpoint.setImId(imId);
            virtualEndpoint.setTag(tag);
            virtualEndpointRepository.save(virtualEndpoint);
        }
        // 标记当前用户的聚焦会话框
        endpointFocusRepository.markCurrentIm(imId, eid);
        // 标记此会话的消息已读
        List<Message> historyMessages = history(imId, eid, null, 1);
        if (historyMessages != null && !historyMessages.isEmpty()) {
            virtualEndpointRepository.trackLastMsgId(imId, eid, historyMessages.get(0).getId(), true);
        }
        return virtualEndpoint;
    }

    private VirtualEndpoint getVirtualEndpoint(String imId, String eid) {
        return virtualEndpointRepository.get(imId, eid);
    }

    @Override
    public MessageBox box(String imId, String eid) {
        VirtualEndpoint virtualEndpoint = getVirtualEndpoint(imId, eid);
        String tag = virtualEndpoint.getTag();
        Long lastReadMsgId = virtualEndpoint.getLastReadMsgId();
        int count = lastReadMsgId == null ? messageRepository.countByImIdAndTagOrTagIsNull(imId, tag) : messageRepository.countByImIdAndMsgIdAfterAndTagOrTagIsNull(imId, lastReadMsgId, tag);
        List<Message> messages = history(imId, eid, null, 1);
        MessageBox messageBox = new MessageBox();
        messageBox.setImId(imId);
        messageBox.setUnread(count);
        if (messages != null && !messages.isEmpty()) {
            Message lastMsg = messages.get(0);
            messageBox.setEid(lastMsg.getEid());
            messageBox.setTimestamp(lastMsg.getTimestamp());
            messageBox.setMessageId(lastMsg.getId());
            messageBox.setContent(lastMsg.getContent());
        }
        return messageBox;
    }

    @Override
    public List<MessageBox> historyBox(String eid, String msgId, int size) {
        List<VirtualEndpoint> virtualEndpoints = virtualEndpointRepository.findAllByEidAndMsgBeforeOrderByMsgIdDesc(eid, msgId, size);
        List<MessageBox> messageBoxes = new ArrayList<>();
        virtualEndpoints.forEach(ve -> {
            String imId = ve.getImId();
            MessageBox box = box(imId, eid);
            messageBoxes.add(box);
        });
        return messageBoxes;
    }

    @Override
    public List<Message> history(String imId, String eid, String msgId, int size) {
        VirtualEndpoint virtualEndpoint = getVirtualEndpoint(imId, eid);
        String tag = virtualEndpoint.getTag();
        List<Message> messages;
        if (msgId == null || "".equals(msgId)) {
            messages = messageRepository.findByImIdAndTagOrTagIsNullOrderByMsgIdDesc(imId, tag, size);
        } else {
            messages = messageRepository.findByImIdAndMsgIdBeforeAndTagOrTagIsNullOrderByMsgIdDesc(imId, Long.parseLong(msgId), tag, size);
        }
        if (!messages.isEmpty()) {
            virtualEndpointRepository.trackLastMsgId(imId, eid, messages.get(0).getId(), true);
        }
        return messages;
    }

    @Override
    public List<Message> historySent(String imId, String eid, String msgId, int size) {
        List<Message> messages;
        if (msgId == null || "".equals(msgId)) {
            messages = messageRepository.findByImIdAndEidOrderByMsgIdDesc(imId, eid, size);
        } else {
            messages = messageRepository.findByImIdAndMsgIdBeforeAndEidOrderByMsgIdDesc(imId, Long.parseLong(msgId), eid, size);
        }
        return messages;
    }
}
