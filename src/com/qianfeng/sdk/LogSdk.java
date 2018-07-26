package com.qianfeng.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Auther: lyd
 * @Date: 2018/7/25 09:29
 * @Description:日志产生的sdk
 */
public class LogSdk {
    //日志打印对象
    private static final Logger logger = Logger.getGlobal();
    //定义常量
    private static final String ver = "1.0";
    private static final String platformName = "java_server";
    private static final String chargeSuccess = "e_cs";
    private static final String chargeRefund = "e_cr";
    private static final String sdkName = "java_sdk";
    private static final String requestUrl = "http://192.168.216.111/index.html/java_server";


    /**
     * 支付成功事件，成功返回true，失败返回false
     * @param mid
     * @param oid
     * @param flag  1:chargeSuccess   2:chargeRefund  默认使用1
     * @return
     */
    public static String chargeSuccess(String mid,String oid,String flag){
        if(isEmpty(mid) || isEmpty(oid)){
            logger.log(Level.WARNING,"mid or oid is null." +
                    "but both is must not null.");
//            return false;
        }
        try{
            //umi oid 肯定不为空  http://192.168.216.111/index.html?en=1.0&pl=java_server
            Map<String,String> data = new HashMap<String,String>();
            if(isEmpty(flag) || flag.equals("1")){
                data.put("en",chargeSuccess);
            } else if(flag.equals("2")) {
                data.put("en",chargeRefund);
            }
            data.put("pl",platformName);
            data.put("sdk",sdkName);
            data.put("c_time",System.currentTimeMillis()+"");
            data.put("ver",ver);
            data.put("u_mid",mid);
            data.put("oid",oid);
            //构造最终请求的url
            String url = buildUrl(data);
            //将url添加到队列中
            SendUrl.getInstance().addUrlToQueue(url);
            String json = "{\"code\":200,\"data\":{\"isSuccess\":true}}";
            return json;
        } catch (Exception e){
            throw  new RuntimeException("请求成功事件失败.");
        }
    }


    /**
     *
     * @param data
     * @return  http://192.168.216.111/index.html?en=1.0&pl=java_server...
     */
    private static String buildUrl(Map<String, String> data) {
        if(data.isEmpty()){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(requestUrl).append("?");
            //循环data
            for (Map.Entry<String,String> en:data.entrySet()) {
                if(isNotEmpty(en.getKey())){
                    sb.append(en.getKey()).append("=").
                            append(URLEncoder.encode(en.getValue(),"UTF-8")).append("&");
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.WARNING,"value的编码异常");
        }
        return sb.toString().substring(0,sb.length()-1);
    }


    /**
     * 判断字符串是否为空，为空返回true，否则false
     * @param input
     * @return
     */
    public static boolean isEmpty(String input){
        return input == null || input.trim().equals("") || input.trim().length() == 0 ? true : false;
    }

    public static boolean isNotEmpty(String input){
        return !isEmpty(input);
    }
}
