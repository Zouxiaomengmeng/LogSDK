package com.qianfeng.sdk;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Auther: lyd
 * @Date: 2018/7/25 10:15
 * @Description: 该类专门用于发送http请求已经构建的好的url
 */
public class SendUrl {
    //日志打印对象
    private static final Logger logger = Logger.getGlobal();
    //定义存储url的队列
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    //创建单利的对象
    private static  SendUrl sendUrl = null;
    //私有的构造器
    private SendUrl(){

    }
    //共有的获取该类的实例的方法
    public static SendUrl getInstance(){
        //先判断sendurl是否为空
        if(sendUrl == null){
            synchronized (SendUrl.class) { //防止有两个线程同时来获取
                if(sendUrl == null){
                    sendUrl = new SendUrl();

                    //创建一个独立线程
                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //真正发送
                            SendUrl.sendUrl.sendUrl();
                        }
                    });
                    //如果需要挂载启动
//                    th.setDaemon(true);  //建议在服务器运行时可以挂载后台启动
                    //将线程启动
                    th.start();
                }
            }
        }
        return sendUrl;
    }

    /**
     * 将url添加自己的发送URl的队列中
     */
    public static void addUrlToQueue(String url){
        try {
            getInstance().queue.put(url);
//            getInstance().queue.add(url);
        } catch (Exception e) {
            logger.log(Level.WARNING,"添加url到队列中异常.");
        }
    }

    /**
     * 循环将队列中的url进行发送
     */
    public static void sendUrl(){
        while (true){
            try {
                String url = getInstance().queue.take();
                HttpUtil.requesUrl(url); //发送
            } catch (InterruptedException e) {
                logger.log(Level.WARNING,"获取队列中url异常.");
            }
        }
    }

    /**
     * 用于发送url的工具类
     */
    public static class HttpUtil{
        /**
         * 发送url
         */
        public static void requesUrl(String url){
            HttpURLConnection conn = null;
            InputStream is = null;
            try{
                //构建url
                URL url1 = new URL(url);
                //获取conn
                conn = (HttpURLConnection) url1.openConnection();
                //为conn设置属性
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(3000);
                //真正的发送
                is = conn.getInputStream();
            } catch (Exception e){
                logger.log(Level.WARNING,"真正请求失败");
            } finally {
                conn.disconnect();
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        //do nothing
                    }
                }
            }
        }
    }
}
