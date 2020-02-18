package com.telerikacademy.socialalien.controllers.restControllers;


import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.helpers.JwtHelpers.JwtRequest;
import com.telerikacademy.socialalien.helpers.JwtHelpers.JwtResponse;
import com.telerikacademy.socialalien.helpers.JwtHelpers.JwtTokenUtil;
import com.telerikacademy.socialalien.helpers.LoginHttpClient;
import com.telerikacademy.socialalien.models.dtos.LoginDto;
import com.telerikacademy.socialalien.services.MyUserDetailsService;
import com.telerikacademy.socialalien.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class LoginRestController {


    private LoginHttpClient httpClient;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;


     public LoginRestController(UserService userService){

         this.httpClient = new LoginHttpClient();

     }



     @GetMapping("/user")
     public Principal requestCurrentlyAuthenticatedUser(Principal principal){
         return principal;
     }


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody LoginDto loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        try{
            httpClient.sendPost(username, password);
        }catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));

        }





}
