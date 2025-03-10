package com.scmanage.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scmanage.entities.User;
import com.scmanage.helpers.ResourceNotFoundException;
import com.scmanage.repository.UserRepo;
import com.scmanage.services.UserService;

import com.scmanage.helpers.AppConstants;

@Service
public class UserServiceIMP implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public User saveUser(User user) {
        //user id = user id has to generate dynamically
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        // password encode
        // user.setPassword(userId);
        // user.setp
        // password encode
        // user.setPassword(userId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // set the user role
        user.setEnabled(true);
        user.setRolelist(List.of(AppConstants.ROLE_USER));
        return userRepo.save(user);
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return userRepo.findById(userId);
    }

    @Override
    public Optional<User> updateUser(User user) {
        User user_object = userRepo.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user not found!"));
        user_object.setPassword(user.getPassword());
        user_object.setName(user.getName());
        user_object.setEmail(user.getEmail());
        user_object.setAbout(user.getAbout());
        user_object.setPhoneNumber(user.getPhoneNumber());
        user_object.setProfilePic(user.getProfilePic());
        user_object.setEnabled(user.isEnabled());
        user_object.setEmailVerified(user.isEmailVerified());
        user_object.setEmailVerified(user.isEmailVerified());
        user_object.setProvider(user.getProvider());
        user_object.setUserId(user.getUserId());
        // save user in database
        User save = userRepo.save(user_object);
        return Optional.ofNullable(save);
    }

    // @Override
    // public void deleteUser(String userId) {
    // User user_object = userrepo.findById(userId).orElseThrow(()-> new
    // ResourceNotFoundException("user not found!"));
    // }
    @Override
    public void deleteUser(String userId) {
        User user_object = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found!"));
        userRepo.delete(user_object);
    }

    @Override
    public boolean isUserExit(String userId) {

        User user_object = userRepo.findById(userId)
                .orElse(null);
        return (user_object!=null) ? true : false;
    }

    @Override
    public boolean isUserExistByEmailId(String email) {
        User user = userRepo.findByEmail(email)
                .orElse(null);
        return user != null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);

    }

}
