package com.miragic.network.tool.net;

import com.miragic.network.tool.listener.MessageReceivedListener;
import com.miragic.network.tool.model.DataManager;
import com.miragic.network.tool.net.handler.TCPHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;

public abstract class AbstractNetTcp extends AbstractNet {
    protected ChannelHandler initializer;

    protected AbstractNetTcp(DataManager data, MessageReceivedListener listener, String netName) {
        super(data, netName);
        initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("encoder", new MessageToByteEncoder<byte[]>() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
                        out.writeBytes(msg);
                    }
                });
                ch.pipeline().addLast("handler", new TCPHandler(data, listener));
            }
        };
        this.data = data;
    }

    @Override
    public boolean needTarget() {
        return false;
    }

    @Override
    public boolean canRemoveClient() {
        return false;
    }
}
