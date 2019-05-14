package cn.laymm.Mail.service;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;

/**
 * Created by laymm.cn on 2019/5/14 0014.
 */
public interface IMailService {
    void sendHtmlMail(String to, String subject, String content) throws MessagingException;
}
