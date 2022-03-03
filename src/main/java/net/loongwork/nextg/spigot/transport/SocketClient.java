package net.loongwork.nextg.spigot.transport;

import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import net.loongwork.nextg.spigot.Loggable;
import net.loongwork.nextg.spigot.events.SocketMessageEvent;
import net.loongwork.nextg.spigot.transport.packets.BasePacket;
import net.loongwork.nextg.spigot.utils.CryptUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketClient implements Loggable {

    private long heartbeatInterval = 10000;

    private long lastHeartbeatTime = 0;

    private Socket client;

    private BufferedWriter out;

    private BufferedReader in;

    private boolean tryToReconnect = true;

    private boolean connected = false;

    private SocketClient() {
    }

    public static SocketClient getInstance() {
        return SocketClientHolder.INSTANCE;
    }

    public void create() {
        try {
            Thread.sleep(2000);
            client = new Socket("localhost", 10000);
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            connected = true;
            listenForMessage();
            startHeartbeat();
            logger().info("[Socket] 已连接到服务器");
        } catch (IOException | InterruptedException e) {
            logger().severe("[Socket] 连接服务器失败！");
        }
    }

    private void startHeartbeat() {
        Schedulers.async().runLater(() -> {
            while (connected && tryToReconnect) {
                try {
                    out.append("heartbeat").append("\n");
                    out.flush();
                    Thread.sleep(heartbeatInterval);
                } catch (InterruptedException e) {
                    tryToReconnect = false;
                } catch (IOException e) {
                    logger().severe("[Socket] 发送心跳失败！");
                }
            }
        }, heartbeatInterval);
    }

    private void listenForMessage() {
        Schedulers.async().run(() -> {
            logger().info("[Socket] 开始监听消息");
            while (connected && client.isConnected()) {
                try {
                    String message = in.readLine();
                    if (message != null) {
                        logger().info("[Socket] 收到消息：" + message);
                        if (message.equals("heartbeat")) {
                            lastHeartbeatTime = System.currentTimeMillis();
                        } else {
                            Events.callAsync(new SocketMessageEvent(message));
                        }
                    }
                } catch (IOException e) {
                    logger().severe("[Socket] 读取消息失败！");
                    e.printStackTrace();
                }
            }
            logger().warning("[Socket] 已停止监听消息，连接可能已断开");
        });
    }

    public void sendMessage(String message) {
        try {
            logger().info("[Socket] 正在发送消息：" + message);
            out.append(CryptUtils.encrypt(message)).append("\n");
            out.flush();
        } catch (IOException e) {
            logger().severe("[Socket] 发送消息失败！");
        }
    }

    public void sendPacket(BasePacket packet) {
        sendMessage(packet.serialize());
    }

    public void close() {
        try {
            connected = false;
            tryToReconnect = false;
            sendMessage("quit");
            in.close();
            out.close();
            client.close();
            logger().info("[Socket] 已断开连接");
        } catch (IOException e) {
            logger().severe("[Socket] 关闭连接失败！");
        }
    }

    private static class SocketClientHolder {
        private static final SocketClient INSTANCE = new SocketClient();
    }
}
