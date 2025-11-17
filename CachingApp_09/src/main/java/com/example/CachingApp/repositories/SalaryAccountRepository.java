package com.example.CachingApp.repositories;

import com.example.CachingApp.entities.SalaryAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SalaryAccountRepository extends CrudRepository<SalaryAccount, Long> {

    @Override
    Optional<SalaryAccount> findById(Long id);
}
