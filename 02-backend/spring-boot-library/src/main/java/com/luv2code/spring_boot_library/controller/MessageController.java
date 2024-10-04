package com.luv2code.spring_boot_library.controller;

import com.luv2code.spring_boot_library.entity.Message;
import com.luv2code.spring_boot_library.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin("http://localhost:3000")
public class MessageController extends AbstractAuthorizationController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {

        this.messageService = messageService;
    }

    @PostMapping("/secure/message")
    public void addMessage(@RequestBody Message message, @RequestHeader("Authorization") String token) throws Exception {

        String userEmail = getUserEmailFromToken(token);
        messageService.createMessage(message, userEmail);
    }
}