package com.ssof.app;

import com.ssof.gui.MainWindow;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Start {
    public static void main(String [] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("ts-main.xml");
        MainWindow mainWindow = applicationContext.getBean("mainWindow", MainWindow.class);
        mainWindow.setVisible(true);
    }
}
