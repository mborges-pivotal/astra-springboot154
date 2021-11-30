package com.datastax.da.astra.controller;

import java.util.List;

import com.datastax.da.astra.model.Account;
import com.datastax.da.astra.model.AccountKey;
import com.datastax.da.astra.model.Position;
import com.datastax.da.astra.repository.AccountRepository;
import com.datastax.da.astra.repository.PositionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvestimentController {

    @Autowired
	private AccountRepository accountRepo;

	@Autowired
	private PositionRepository positionRepo;

    
    @RequestMapping(value = "/accounts/{username}", method = RequestMethod.GET)
    public List<Account> listAccounts(@PathVariable("username") String userName) {
        return accountRepo.findByKeyUserName(userName);
    }

    @RequestMapping(value = "/positions/{account}", method = RequestMethod.GET)
    public List<Position> listPositionsByAccount(@PathVariable String account) {
        return positionRepo.findByKeyAccount(account);
    }


    
}
