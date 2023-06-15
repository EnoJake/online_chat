package com.fengfengzi.chatserver.listen;

/**
 * @author 王丰
 * @version 1.0
 */

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.fengfengzi.chatserver.pojo.vo.NewMessageVo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Component
@Transactional(rollbackFor = Throwable.class)
public class SocketIOListener {

    @Resource
    private SocketIOServer socketIOServer;


    @OnConnect
    public void onConnect(SocketIOClient client) {
        //System.out.println("SocketIoHandle 收到连接：" + client.getSessionId());
    }

    /**
     * 客户端断开socket服务器时执行此事件
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        //System.out.println("当前链接关闭：" + client.getSessionId());
    }


    @OnEvent(value = "sendNewMessage")
    public void sendNewMessage(SocketIOClient client, String message) {
        System.out.println("get new message  " + client.getSessionId() + " " + message);

        socketIOServer.getBroadcastOperations().sendEvent("getMessage", message);
    }

    @OnEvent("chatMessage")
    public void handleChatMessage(SocketIOClient client, String message) {
        // 处理接收到的 chatMessage 事件
        System.out.println("Received chat message from client: " + message);

        // 在这里可以对接收到的消息进行处理，或者向客户端发送响应
        // ...
    }

}
