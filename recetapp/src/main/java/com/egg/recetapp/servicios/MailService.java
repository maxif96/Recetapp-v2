/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.servicios;

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
    private JavaMailSender MS;
    
    @Async//el hilo de la ejecucion del programa no se detiene,manda el email paralelamente,mientras el programa se sigue ejecutando
    public void send(String cuerpo, String titulo, String mail){
        SimpleMailMessage mensaje= new SimpleMailMessage ();//es un objeto de mailSender
        mensaje.setTo(mail);
        mensaje.setFrom("maxirecetapp99@outlook.com.ar");
        mensaje.setSubject(titulo);
        mensaje.setText(cuerpo);
    //se le puede agregar fecha o demas cosas
        
        MS.send(mensaje);
    }
    
}
