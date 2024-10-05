package com.luv2code.spring_boot_library.controller;

import com.luv2code.spring_boot_library.requestmodels.AddBookRequest;
import com.luv2code.spring_boot_library.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("https://localhost:3000")
@RequestMapping("/api/admin")
@RestController
public class AdminController extends AbstractAuthorizationController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {

        this.adminService = adminService;
    }

    @PostMapping(value = "/secure/add/book")
    public void addBook(@RequestBody AddBookRequest addBookRequest, @RequestHeader("Authorization") String token) throws Exception {

        assertAdminUser(token);
        adminService.addBook(addBookRequest);
    }

    @PutMapping(value = "/secure/book/quantity")
    public void updateBookQuantity(@RequestParam Long bookId, @RequestParam int quantity, @RequestHeader("Authorization") String token) throws Exception {

        assertAdminUser(token);
        adminService.updateBookQuantity(bookId, quantity);
    }

    @DeleteMapping("/secure/delete/book")
    public void deleteBook(@RequestParam Long bookId, @RequestHeader("Authorization") String token) throws Exception {

        assertAdminUser(token);
        adminService.deleteBook(bookId);
    }
}
