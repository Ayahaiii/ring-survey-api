package com.monetware.ringsurvey.system.util.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Simo
 * @date 2019-03-13
 */
@Component
@Slf4j
@ServerEndpoint(value = "/websocket/{userId}")
public class WebSocketUtil {


    /**
     * 线程安全Set,存放每个客户端对应的WebSocketService对象
     */
    private static CopyOnWriteArraySet<WebSocketUtil> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 与某个客户端的连接会话，通过它给客户端发送数据
     */
    private Session session;

    private String userId;

    /**
     * 群发自定义消息
     *
     * @param message
     * @throws IOException
     */
    public static void sendInfo(String message) throws IOException {
        for (WebSocketUtil item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    /**
     * 连接建立成功调用的方法
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) throws IOException {
        this.session = session;
        this.userId = userId;
        WebSocketUtil _this = getcurrentWenSocket(this.userId);
        if(_this != null){
            //sendMessage("您已有连接信息，不能重复连接 !");
            return;
        }
        webSocketSet.add(this);
        log.info("有新连接加入！");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);

        log.info("关闭了一个连接。");
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 主动关闭连接
        if (null != message && message.equals("CLOSE")) {
            try {
                this.session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            //群发消息
            for (WebSocketUtil item : webSocketSet) {
                try {
                    if (this.userId == item.userId) {
                        item.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("=================发生错误====================");
        error.printStackTrace();
    }

    /**
     * 发送消息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 判断是否有当前用户
     * @param userId
     * @return
     */
    public static WebSocketUtil getcurrentWenSocket(String userId){
        if(userId == null || "".equals(userId)  || webSocketSet == null || webSocketSet.size() < 1){
            return null;
        }
        Iterator<WebSocketUtil> iterator = webSocketSet.iterator();
        while (iterator.hasNext()) {
            WebSocketUtil _this = iterator.next();
            if(_this.userId.equals(userId)){
                return _this;
            }
        }
        return null;
    }

    /**
     * 发送单条消息
     * @param userId
     * @param message
     */
    public static void sendMessage(String userId,String message){
        try {
            if(userId == null || "".equals(userId) || StringUtils.isEmpty(message)){
                return;
            }
            WebSocketUtil _this = getcurrentWenSocket(userId);
            if(_this == null){
                return;
            }
            _this.sendMessage(message);
        } catch (IOException e) {
            log.info("发送消息异常！");
        }
    }
}
