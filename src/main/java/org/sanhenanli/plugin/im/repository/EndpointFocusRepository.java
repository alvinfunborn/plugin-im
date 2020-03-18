package org.sanhenanli.plugin.im.repository;

/**
 * datetime 2020/3/11 13:58
 * 终端聚焦记录
 *
 * @author zhouwenxiang
 */
public interface EndpointFocusRepository {

    /**
     * 获取终端当前聚焦的会话
     * @param eid 终端id
     * @return 会话id
     */
    String currentIm(String eid);

    /**
     * 记录终端当前聚焦的会话
     * @param imId 会话id
     * @param eid 终端id
     */
    void markCurrentIm(String imId, String eid);
}
