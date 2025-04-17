package com.example.Attendence.controller;
import com.example.Attendence.Utility.TimeBasedTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/token")
    public String getToken(@RequestParam(required = false) String status) {
        // Simulate token generation (Replace this with actual token logic)
      //  String token = "generated-token-12345";
        String token = TimeBasedTokenUtil.generateToken();
        // Log the received status (optional)
       // System.out.println("Received status: " + token);


       // return ResponseEntity.ok("535");

        return token;
    }
}
