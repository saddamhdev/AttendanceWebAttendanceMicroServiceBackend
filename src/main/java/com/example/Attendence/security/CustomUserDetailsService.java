package com.example.Attendence.security;

import com.example.Attendence.model.Employee;
import com.example.Attendence.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserService userService;
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        throw new UsernameNotFoundException("User not found");
    }

    public UserDetails loadUserByUsername(String username,String header) throws UsernameNotFoundException {
        List<Employee> data = userService.employeeList(header);

           Employee gg=data.stream()
                   .filter(employee -> employee.getEmail().equals(username) && employee.getStatus().equals("1"))
                   .findFirst()
                   .orElse(null);
           if(gg!=null){

               List<SimpleGrantedAuthority> authorities = new ArrayList<>();
               if(gg.getType() != null && !gg.getType().isEmpty()){
                   gg.getType().forEach(e->{
                       authorities.add(new SimpleGrantedAuthority(e));
                   });
               }
              

               authorities.add(new SimpleGrantedAuthority("SNVN"));

               return new User(username, gg.getPassword(), authorities);  // Returning user with roles
           }
        throw new UsernameNotFoundException("User not found");
    }



}

