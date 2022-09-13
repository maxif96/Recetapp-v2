/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author Fabi
 */
@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;
    
    @Async
    public void send(String cuerpo, String titulo, String mail){
        SimpleMailMessage message = new SimpleMailMessage ();//es un objeto de mailSender
        message.setTo(mail);
        message.setFrom("maxirecetapp99@outlook.com.ar");
        message.setSubject(titulo);
        message.setText(cuerpo);

        mailSender.send(message);
    }
    
}
