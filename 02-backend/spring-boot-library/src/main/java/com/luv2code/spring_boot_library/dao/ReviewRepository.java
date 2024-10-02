package com.luv2code.spring_boot_library.dao;

import com.luv2code.spring_boot_library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Book, Long> {

}