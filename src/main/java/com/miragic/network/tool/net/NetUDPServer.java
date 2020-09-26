package com.miragic.network.tool.net;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import com.miragic.network.tool.ui.select.INetSocketAddressOption;
import com.miragic.network.tool.listener.MessageReceivedListener;
import com.miragic.network.tool.model.DataManager;
import com.miragic.network.tool.net.handler.UDPHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class NetUDPServer extends AbstractNet {
    private Bootstrap b;

    public NetUDPServer(DataManager data, MessageReceivedListener listener) {
        super(data, "UDP");
        b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true)
                .handler(new UDPHandler(data, listener));
    }

    @Override
    public boolean start(String ip, int port) {
        try {
            ChannelFuture f = b.bind(ip, port).sync();
            channel = f.channel();
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null, "服务器异常", "服务器异常", JOptionPane.OK_OPTION);
            return false;
        } catch (Exception e) {
            if (e instanceof BindException) {
                JOptionPane.showMessageDialog(null, "端口[" + port + "]已经被占用", "参数异常", JOptionPane.OK_OPTION);
            }
            return false;
        }
        return true;
    }

    @Override
    public void sendMsg(String text) {
        byte[] bytes = msg2Bytes(text);
        Supplier<InetSocketAddress> addressSource = data.getAddressSource();
        if (addressSource == null) {
            List<Object> connects = data.getConnections();
            for (Object connect : connects) {
                if (connect instanceof INetSocketAddressOption) {
                    INetSocketAddressOption net = (INetSocketAddressOption) connect;
                    InetSocketAddress add = net.getAddress();
                    sendMsg(bytes, add);
                } else {
                    System.err.println("UDP 连接列表里有非UDP连接");
                }
            }
        } else {
            InetSocketAddress add = addressSource.get();
            if (add != null) {
                sendMsg(bytes, add);
            }
        }
    }

    private void sendMsg(byte[] bytes, InetSocketAddress add) {
        DatagramPacket packet = new DatagramPacket(Unpooled.wrappedBuffer(bytes), add);
        channel.writeAndFlush(packet);
    }

    @Override
    public boolean isServer() {
        return true;
    }

    @Override
    public boolean canRemoveClient() {
        return true;
    }

    @Override
    public boolean needTarget() {
        return true;
    }
}
