# IM即时通讯插件

IM消息管理代码块, 实现了发送消息, 实时会话列表, 已读未读, 拉取消息记录等功能, 会话中支持广播消息和指向性消息

#### 概念描述
```
graph TB
im1[会话1]-->ve1[虚拟终端A1]
im2[会话2]-->ve2[虚拟终端A2]
im3[会话3]-->ve3[虚拟终端A3]
ve1-->e(终端A)
ve2-->e
ve3-->e
im3-->ve4[虚拟终端B1]
ve4-->eb(终端B)
```

#### 数据流描述
```
sequenceDiagram
客户端->>服务: 进入会话框
服务->>im插件: 根据该会话唯一票据和用户id注册虚拟终端
im插件->>服务: 返回虚拟终端
服务->>客户端: 返回虚拟终端
客户端->>im插件: 拉取虚拟终端的消息记录
im插件->>客户端: 返回消息记录
客户端->>客户端: 会话中消息左右分发
客户端->>im插件: 发送消息
im插件->>推送服务: 推送消息
推送服务->>客户端: 向终端推送消息
客户端->>客户端: 向各会话分发消息
```

#### 使用示例

1. 实现MessageRepository, 消息管理的数据层支持
2. 实现VirtualEndpointRepository, 虚拟终端的数据层支持
3. 实现EndpointFocusRepository, 支持已读未读功能
4. 实现ImPusher, 消息推送器

##### config

```
@Configuration
public class ImConfig {

    @Autowired
    private TogoClient togoClient;
    @Autowired
    private IImMessageDao imMessageDao;
    @Autowired
    private IImVirtualEndpointDao imVirtualEndpointDao;
    @Autowired
    private IImEndpointFocusDao imEndpointFocusDao;

    @Bean
    public ImService imService() {
        return new SimpleImServiceImpl(messageRepository(), virtualEndpointRepository(), endpointFocusRepository(), imPusher());
    }

    @Bean
    public MessageRepository messageRepository() {
        return new MessageRepository() {
            @Override
            public Message save(Message message) {
                ImMessageEntity entity = parse(message);
                imMessageDao.save(entity);
                return extract(entity);
            }

            @Override
            public int countByImIdAndMsgIdAfterAndTagOrTagIsNull(String imId, long msgId, String tag) {
                return (int) imMessageDao.countByImIdAndMsgIdAfterAndTagOrTagIsNull(imId, msgId, tag);
            }

            @Override
            public List<Message> findByImIdAndTagOrTagIsNull(String imId, String tag, int size) {
                IPage<ImMessageEntity> entities = imMessageDao.findByImIdAndTagOrTagIsNull(imId, tag, size);
                return entities.getRecords().stream().map(this::extract).collect(Collectors.toList());
            }

            @Override
            public List<Message> findByImIdAndMsgIdBeforeAndTagOrTagIsNull(String imId, long msgId, String tag, int size) {
                IPage<ImMessageEntity> entities = imMessageDao.findByImIdAndMsgIdBeforeAndTagOrTagIsNull(imId, msgId, tag, size);
                return entities.getRecords().stream().map(this::extract).collect(Collectors.toList());
            }

            @Override
            public List<Message> findByImIdAndEid(String imId, String eid, int size) {
                IPage<ImMessageEntity> entities = imMessageDao.findByImIdAndEid(imId, eid, size);
                return entities.getRecords().stream().map(this::extract).collect(Collectors.toList());
            }

            @Override
            public List<Message> findByImIdAndMsgIdBeforeAndEid(String imId, long msgId, String eid, int size) {
                IPage<ImMessageEntity> entities = imMessageDao.findByImIdAndMsgIdBeforeAndEid(imId, msgId, eid, size);
                return entities.getRecords().stream().map(this::extract).collect(Collectors.toList());
            }

            private ImMessageEntity parse(Message message) {
                if (message == null) {
                    return null;
                }
                ImMessageEntity entity = new ImMessageEntity();
                BeanUtils.copyProperties(message, entity);
                return entity;
            }

            private Message extract(ImMessageEntity entity) {
                if (entity == null) {
                    return null;
                }
                Message message = new Message();
                BeanUtils.copyProperties(entity, message);
                message.setId(entity.getId());
                return message;
            }
        };
    }

    @Bean
    public VirtualEndpointRepository virtualEndpointRepository() {
        return new VirtualEndpointRepository() {
            @Override
            public VirtualEndpoint save(VirtualEndpoint virtualEndpoint) {
                ImVirtualEndpointEntity entity = parse(virtualEndpoint);
                ImVirtualEndpointEntity existed = imVirtualEndpointDao.findByImIdAndEid(virtualEndpoint.getImId(), virtualEndpoint.getEid());
                if (existed != null) {
                    entity.setId(existed.getId());
                    imVirtualEndpointDao.updateById(entity);
                } else {
                    imVirtualEndpointDao.save(entity);
                }
                return extract(entity);
            }

            @Override
            public VirtualEndpoint get(String imId, String eid) {
                return extract(imVirtualEndpointDao.findByImIdAndEid(imId, eid));
            }

            @Override
            public VirtualEndpoint trackLastMsgId(String imId, String eid, long msgId, boolean read) {
                ImVirtualEndpointEntity entity = imVirtualEndpointDao.findByImIdAndEid(imId, eid);
                entity.setLastMsgId((int) msgId);
                if (read) {
                    entity.setLastReadMsgId((int) msgId);
                }
                imVirtualEndpointDao.updateById(entity);
                return extract(entity);
            }

            @Override
            public List<VirtualEndpoint> findAllByImId(String imId) {
                return imVirtualEndpointDao.findByImId(imId).stream().map(this::extract).collect(Collectors.toList());
            }

            @Override
            public List<VirtualEndpoint> findAllByEidAndMsgBeforeOrderByMsgIdDesc(String eid, String msgId, int size) {
                if (msgId == null) {
                    return imVirtualEndpointDao.findByEidOrderByMsgIdDesc(eid, size).getRecords().stream().map(this::extract).collect(Collectors.toList());
                }
                return imVirtualEndpointDao.findByEidAndMsgIdLtOrderByMsgIdDesc(eid, Long.parseLong(msgId), size).getRecords().stream().map(this::extract).collect(Collectors.toList());
            }

            private ImVirtualEndpointEntity parse(VirtualEndpoint endpoint) {
                if (endpoint == null) {
                    return null;
                }
                ImVirtualEndpointEntity entity = new ImVirtualEndpointEntity();
                BeanUtils.copyProperties(endpoint, entity);
                entity.setId(endpoint.getId() == null ? null : Integer.parseInt(endpoint.getId()));
                entity.setLastMsgId(endpoint.getLastMsgId() == null ? null : endpoint.getLastMsgId().intValue());
                entity.setLastReadMsgId(endpoint.getLastReadMsgId() == null ? null : endpoint.getLastReadMsgId().intValue());
                return entity;
            }

            private VirtualEndpoint extract(ImVirtualEndpointEntity entity) {
                if (entity == null) {
                    return null;
                }
                VirtualEndpoint virtualEndpoint = new VirtualEndpoint();
                BeanUtils.copyProperties(entity, virtualEndpoint);
                virtualEndpoint.setId(String.valueOf(entity.getId()));
                virtualEndpoint.setLastMsgId(entity.getLastMsgId() == null ? null : entity.getLastMsgId().longValue());
                virtualEndpoint.setLastReadMsgId(entity.getLastReadMsgId() == null ? null : entity.getLastReadMsgId().longValue());
                return virtualEndpoint;
            }
        };
    }

    @Bean
    public EndpointFocusRepository endpointFocusRepository() {
        return new EndpointFocusRepository() {
            @Override
            public String currentIm(String eid) {
                ImEndpointFocusEntity entity = imEndpointFocusDao.getById(eid);
                return entity == null ? null : entity.getImId();
            }

            @Override
            public void markCurrentIm(String imId, String eid) {
                ImEndpointFocusEntity entity = imEndpointFocusDao.getById(eid);
                if (entity == null) {
                    entity = new ImEndpointFocusEntity();
                    entity.setEid(eid);
                    entity.setImId(imId);
                    imEndpointFocusDao.save(entity);
                } else {
                    entity.setImId(imId);
                    imEndpointFocusDao.updateById(entity);
                }
            }
        };
    }

    @Bean
    public ImPusher imPusher() {
        return (eid, data) -> {
            boolean connected = togoClient.connected(TogoTunnelEnum.WEBSOCKET, eid);
            if (connected) {
                togoClient.add(eid, TogoBusinessEnum.ONLINE_INQUIRY_IM, null, data, TogoTunnelEnum.WEBSOCKET);
                return true;
            }
            return false;
        };
    }
}
```

##### service

```
@Service
public class TestService {
    
    @Autowired
    private ImService imService;

    public void test() {

    }
}
```