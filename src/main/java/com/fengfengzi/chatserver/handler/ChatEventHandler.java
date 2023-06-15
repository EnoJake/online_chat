package com.fengfengzi.chatserver.handler;

/**
 * @author 王丰
 * @version 1.0
 */
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.fengfengzi.chatserver.pojo.vo.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatEventHandler {

    private final SocketIOServer socketIOServer;

    @Autowired
    public ChatEventHandler(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        // 处理客户端连接事件
        System.out.println("Client connected: " + client.getSessionId());
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        // 处理客户端断开连接事件
        System.out.println("Client disconnected: " + client.getSessionId());
    }

    @OnEvent("chatMessage")
    public void onChatMessage(SocketIOClient client, AckRequest ackRequest, ChatMessage chatMessage) {
        // 处理收到的聊天消息事件
        System.out.println("Received chat message: " + chatMessage.getMessage());

        // 广播消息给所有连接的客户端
        socketIOServer.getBroadcastOperations().sendEvent("chatMessage", chatMessage);
    }
}
