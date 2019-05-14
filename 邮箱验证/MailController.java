package cn.laymm.Mail.controller;

import cn.laymm.Mail.service.IMailService;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.rmi.Remote;
import java.util.Random;

/**
 * Created by laymm.cn on 2019/5/14 0014.
 */
@RestController
@RequestMapping("/mail")
public class MailController {

    @Resource
    IMailService mailService;
    @RequestMapping("/test")
    private String test(){
        String to = "1746619421@qq.com";
        String subject = "邮箱验证";
        String str = "abco12pqx3vw4st56dijklmn7efg8hru9yz0";
        Random random = new Random();
        String code = "";
        for (int i = 0;i<4;i++){
            code+=str.charAt(random.nextInt(str.length()));
        }
        String content = "<html><head></head><body><h3>您的验证码为:<strong style='color:red;margin-left:5px'>"+code+"</strong></h3></body></html>";
        try {
            mailService.sendHtmlMail(to, subject, content);
            return "发送成功";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "发送失败";
        }
    }
}
