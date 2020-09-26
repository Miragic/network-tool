package com.miragic.network.tool.ui.select;

import javax.swing.JComboBox;

import com.miragic.network.tool.net.NetTCPServer;
import com.miragic.network.tool.net.NetUDPServer;
import com.miragic.network.tool.listener.MessageReceivedListener;
import com.miragic.network.tool.model.DataManager;
import com.miragic.network.tool.net.INet;
import com.miragic.network.tool.net.NetTCPClient;

@SuppressWarnings("serial")
public class NetSelect extends JComboBox<INet> {
    public NetSelect(DataManager data) {
        //数据监听器，在收到数据之后，添加到数据中心
        MessageReceivedListener messageReceived = (bytes) -> {
            data.addMessage(bytes);
            ;
        };
        // 网络类型集合
        addItem(new NetTCPServer(data, messageReceived));
        addItem(new NetTCPClient(data, messageReceived));
        addItem(new NetUDPServer(data, messageReceived));
    }

    @Override
    public INet getSelectedItem() {
        return (INet) super.getSelectedItem();
    }
}
