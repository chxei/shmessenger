package org.chxei.shmessenger.service;


import org.chxei.shmessenger.entity.User;
import org.chxei.shmessenger.repository.UserRepository;
import org.chxei.shmessenger.repository.UserRepositoryCustom;
import org.chxei.shmessenger.utils.Misc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class UserService {
    Calendar calendar = Calendar.getInstance();
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRepositoryCustom userRepositoryCustom;

    private UserService() {
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(int id) {
        return userRepository.findById(id).get();
    }

    public User getUserByUsername(String userName) {
        return userRepository.findByUserName(userName).get();
    }

    public void registerUser(User user) {
        user.setPassword(Misc.stringToMd5(user.getPassword()));
        if (userRepositoryCustom.existsByUserName(user.getUserName())) {
            //todo throw error
        } else {
            userRepository.save(user);
        }
    }

    //todo check if username exists before registering
}
