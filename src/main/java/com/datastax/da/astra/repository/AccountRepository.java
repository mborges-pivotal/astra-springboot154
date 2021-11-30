package com.datastax.da.astra.repository;

import java.util.List;

import com.datastax.da.astra.model.Account;
import com.datastax.da.astra.model.AccountKey;

import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, AccountKey> {

    List<Account> findByKeyUserName(String userName);
    
}
