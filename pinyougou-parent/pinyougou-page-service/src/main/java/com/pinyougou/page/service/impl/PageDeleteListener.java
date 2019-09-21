package com.pinyougou.page.service.impl;

import com.pinyougou.page.serveice.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class PageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println("itemDeleteListener监听收到消息..."+goodsIds);
            boolean b = itemPageService.deleteitemHtml(goodsIds);
            System.out.println("网页删除结果" + b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
