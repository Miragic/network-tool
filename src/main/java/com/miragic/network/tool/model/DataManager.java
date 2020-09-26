package com.miragic.network.tool.model;

import java.awt.Component;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.miragic.network.tool.ui.select.ChannelOption;
import com.miragic.network.tool.ui.select.INetSocketAddressOption;

import io.netty.channel.Channel;

public class DataManager {
    private List<String> columnNames = new ArrayList<>();
    private List<Integer> showIndex = new ArrayList<>();
    private List<Object[]> list = new ArrayList<>();
    private Map<String, Integer> map = new HashMap<>();
    private Component component;
    private DataType readType;
    private DataType writeType;
    private Charset charset;
    private Supplier<List<Object>> channelSource;

    private Supplier<InetSocketAddress> addressSource;

    public static enum DataType {
        HEX, STRING
    }

    private BiConsumer<Object, String> consumer;

    public DataManager(Object[][] data, Object[] columnNames) {
        int i = 0;
        for (Object columnName : columnNames) {
            String str = columnName.toString();
            map.put(str, this.columnNames.size());
            this.columnNames.add(str);
            addIndex(i++);
        }
        for (Object[] d : data) {
            check(d);
            list.add(d);
        }
        readType = DataType.STRING;
        writeType = DataType.STRING;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    private void checkIndex(int i) {
        if (i < 0 || i > columnNames.size()) {
            throw new RuntimeException("index out of bound,index:" + i + ",size:" + columnNames.size());
        }
    }

    public void addIndex(int i) {
        checkIndex(i);
        if (!showIndex.contains(i)) {
            showIndex.add(i);
        }
        if (component != null) {
            component.repaint();
        }
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public String getColumnName(int index) {
        int i = showIndex.get(index);
        return columnNames.get(i);
    }

    public int getColumnCount() {
        return showIndex.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        int i = showIndex.get(columnIndex);
        return list.get(rowIndex)[i];
    }

    public int getRowCount() {
        return list.size();
    }

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void addMessage(byte[] bytes) {
        Date d = new Date();
        String sendTime = format.format(d);
        Data data = new Data(bytes, this);
        add(new Object[]{sendTime, data});
    }

    public void add(Object[] d) {
        check(d);
        list.add(d);
        component.repaint();
    }

    private void check(Object[] d) {
        if (d.length != columnNames.size()) {
            throw new RuntimeException("数据长度与列长度不相同!");
        }
    }

    public void addConnect(Channel channel) {
        consumer.accept(new ChannelOption(channel), "add");
    }

    public void addConnect(InetSocketAddress address) {
        consumer.accept(new INetSocketAddressOption(address), "add");
    }

    public void removeConnect(Channel channel) {
        consumer.accept(new ChannelOption(channel), "remove");
    }

    public void removeConnect(InetSocketAddress address) {
        consumer.accept(new INetSocketAddressOption(address), "remove");
    }

    public Component getComponent() {
        return component;
    }

    public DataType getReadType() {
        return readType;
    }

    public void setReadType(DataType readType) {
        this.readType = readType;
    }

    public DataType getWriteType() {
        return writeType;
    }

    public void setWriteType(DataType writeType) {
        this.writeType = writeType;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public int getColumnIndex(String columnName) {
        return map.get(columnName);
    }

    public void removeIndex(Integer index) {
        showIndex.remove(index);
    }

    public List<Object> getConnections() {
        return channelSource.get();
    }

    public void clear() {
        consumer.accept(null, "clear");
    }

    public void setChannelSource(Supplier<List<Object>> channelSource) {
        this.channelSource = channelSource;
    }

    public Supplier<InetSocketAddress> getAddressSource() {
        return addressSource;
    }

    public void setAddressSource(Supplier<InetSocketAddress> addressSource) {
        this.addressSource = addressSource;
    }

    public void setConsumer(BiConsumer<Object, String> consumer) {
        this.consumer = consumer;
    }
}
