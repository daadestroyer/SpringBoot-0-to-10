package com.example.spring_profiles_08.service.impl;

import com.example.spring_profiles_08.service.DataService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class ProdDataService implements DataService {
    @Override
    public String getData() {
        return "Prod Data";
    }
}
