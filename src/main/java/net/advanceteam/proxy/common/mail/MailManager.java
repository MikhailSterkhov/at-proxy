package net.advanceteam.proxy.common.mail;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public final class MailManager {

    @Getter
    private final String smtpHost = "smtp.yandex.ru";

    private final Map<String, MailSender> senderMap = new HashMap<>();


    /**
     * Получение MailSender из кеша.
     *
     * Если его там нет, то он автоматически туда добавляется,
     * возвращая тот объект, что был добавлен туда.
     *
     * @param username - имя пользователя отправителя
     * @param password - пароль отправилеля
     */
    public MailSender getMailSender(String username, String password) {
        return senderMap.computeIfAbsent(username, f -> new MailSender(username, username, password, smtpHost));
    }

    /**
     * Отправить сообщение на почту
     *
     * @param subject - тема сообщения
     * @param content - содержимое сообщения
     * @param toMail - email адрес получателя
     */
    public void sendMessage(MailSender mailSender, String subject, String content, String toMail) {
        mailSender.sendMessage(subject, content, toMail);
    }

}
