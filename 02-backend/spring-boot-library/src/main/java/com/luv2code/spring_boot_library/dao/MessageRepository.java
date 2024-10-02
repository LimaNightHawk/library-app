package com.luv2code.spring_boot_library.dao;

import com.luv2code.spring_boot_library.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}