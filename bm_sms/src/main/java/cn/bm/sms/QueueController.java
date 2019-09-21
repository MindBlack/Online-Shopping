package cn.bm.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class QueueController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/sendsms")
    public void sendSms(){

        Map map = new HashMap();
        map.put("mobile","15170939335");
        map.put("template_code","SMS_174027017");
        map.put("sign_name","优购变身装扮");
        map.put("param","{\"code\":\"586345\"}");
        jmsMessagingTemplate.convertAndSend("sms",map);


    }

}
