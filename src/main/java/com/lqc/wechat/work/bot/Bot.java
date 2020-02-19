package com.lqc.wechat.work.bot;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.lqc.wechat.work.bot.exception.BotException;
import com.lqc.wechat.work.bot.msg.MarkdownMsg;
import com.lqc.wechat.work.bot.msg.TextMsg;
import com.lqc.wechat.work.bot.msg.BotMsg;

/**
 * 企业微信机器人对象
 */
public class Bot {

    /**
     * json配置
     */
    private static final SerializeConfig config;

    static {
        config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    /**
     * 企业微信群中获取的webhook地址
     */
    private String webhook;

    /**
     * 超时时间
     */
    private int timeout;

    public Bot(String webhook) {
        this.webhook = webhook;
        this.timeout = 5*1000;
    }

    public Bot(String webhook,int timeout) {
        this.webhook = webhook;
        this.timeout = timeout;
    }

    public void send(TextMsg textMsg) {
        BotMsg botMsg = new BotMsg(textMsg);
        doPost(botMsg);
    }

    public void send(MarkdownMsg markdownMsg) {
        BotMsg botMsg = new BotMsg(markdownMsg);
        doPost(botMsg);
    }

    /**
     * 发送消息
     * @param botMsg
     */
    public void doPost(BotMsg botMsg){
        try {
            String jsonStr = JSON.toJSONString(botMsg, config);
            String body = HttpRequest.post(webhook)
                    .header(Header.CONTENT_TYPE, ContentType.JSON.toString())
                    .body(jsonStr)
                    .timeout(timeout)
                    .execute()
                    .body();
            JSONObject jsonObject = JSONUtil.parseObj(body);
            if (jsonObject.getInt("errcode") != 0) {
                throw new BotException(jsonObject.getInt("errcode")+" "+jsonObject.getStr("errmsg"));
            }
        } catch (Exception e) {
            throw new BotException(e.getMessage());
        }
    }


}

