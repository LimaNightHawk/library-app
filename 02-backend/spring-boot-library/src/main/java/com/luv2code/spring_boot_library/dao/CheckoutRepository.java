package com.luv2code.spring_boot_library.dao;

import com.luv2code.spring_boot_library.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    Checkout findByUserEmailAndBookId(@RequestParam String userEmail, @RequestParam Long bookId);

    List<Checkout> findByUserEmail(@RequestParam String userEmail);

    @Modifying
    @Query("delete from Checkout c where c.bookId =:bookId")
    void deleteAllByBookId(@Param("bookId") Long bookId);
}