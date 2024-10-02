package com.luv2code.spring_boot_library.dao;

import com.luv2code.spring_boot_library.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {

}