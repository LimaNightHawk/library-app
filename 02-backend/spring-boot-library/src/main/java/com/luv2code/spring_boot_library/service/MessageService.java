package com.luv2code.spring_boot_library.service;

import com.luv2code.spring_boot_library.dao.MessageRepository;
import com.luv2code.spring_boot_library.entity.Message;
import com.luv2code.spring_boot_library.requestmodels.AdminQuestionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {

        this.messageRepository = messageRepository;
    }

    public void createMessage(Message messageRequest, String userEmail) {

        Message message = new Message(messageRequest.getTitle(), messageRequest.getQuestion());
        message.setUserEmail(userEmail);
        messageRepository.save(message);
    }

    public void answerQuestion(AdminQuestionRequest request, String adminEmail) throws Exception {

        Optional<Message> messageOptional = messageRepository.findById(request.getId());
        if (messageOptional.isEmpty()) {
            throw new Exception("Invalid message id");
        }

        Message message = messageOptional.get();
        message.setResponse(request.getResponse());
        message.setAdminEmail(adminEmail);
        message.setClosed(true);
        messageRepository.save(message);
    }
}
