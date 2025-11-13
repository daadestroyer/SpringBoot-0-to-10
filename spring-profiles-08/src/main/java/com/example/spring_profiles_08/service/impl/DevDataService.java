package com.example.spring_profiles_08.service.impl;

import com.example.spring_profiles_08.service.DataService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class DevDataService implements DataService {
    @Override
    public String getData() {
        return "Dev Data";
    }
}
