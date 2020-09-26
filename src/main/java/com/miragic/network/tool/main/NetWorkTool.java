package com.miragic.network.tool.main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.miragic.network.tool.ui.Window;

public class NetWorkTool {

    public static void main(String[] args) {
        //输入中文的时候，不会显示浮动框。如果需要
//      System.setProperty("java.awt.im.style","on-the-spot");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        new Window();
    }
}
