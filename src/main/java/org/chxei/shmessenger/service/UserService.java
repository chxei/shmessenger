package org.chxei.shmessenger.service;


import org.chxei.shmessenger.entity.User;
import org.chxei.shmessenger.repository.UserRepository;
import org.chxei.shmessenger.utils.Misc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class UserService {
    Calendar calendar = Calendar.getInstance();
    @Autowired
    UserRepository userRepositoryImpl;

    private UserService(){}

    public List<User> getAllUsers (){
        return (List<User>) userRepositoryImpl.findAll();
    }

    public User getUser(long id){
        return userRepositoryImpl.findById(id).get();
    }

    public User getUserByUsername(String userName){
        return userRepositoryImpl.findByUserName(userName).get();
    }

    public void registerUser(User user) {
        user.setPassword(Misc.stringToMd5(user.getPassword()));
        if (userRepositoryImpl.existsByUserName(user.getUserName())){
            //todo throw error
        } else {
            userRepositoryImpl.save(user);
        }
    }

    //todo check if username exists before registering
}
