package org.sanhenanli.plugin.im.service;

/**
 * datetime 2020/3/10 14:48
 * 推送器
 *
 * @author zhouwenxiang
 */
public interface ImPusher {

    /**
     * 推送消息
     * @param eid 接收终端id
     * @param data 消息体
     * @return true推送成功, false推送失败
     */
    boolean push(String eid, String data);
}
