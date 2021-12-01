package com.datastax.da.astra.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.datastax.da.astra.model.Account;
import com.datastax.da.astra.model.Position;
import com.datastax.da.astra.model.Trade;
import com.datastax.da.astra.model.trade.TradeD;
import com.datastax.da.astra.model.trade.TradeKey;
import com.datastax.da.astra.repository.AccountRepository;
import com.datastax.da.astra.repository.PositionRepository;
import com.datastax.da.astra.repository.TradeDRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvestimentController {

    @Autowired
	private AccountRepository accountRepo;

	@Autowired
	private PositionRepository positionRepo;

	@Autowired
	private TradeDRepository tradeDRepo;

    
    @RequestMapping(value = "/accounts/{username}", method = RequestMethod.GET)
    public List<Account> listAccounts(@PathVariable("username") String userName) {
        return accountRepo.findByKeyUserName(userName);
    }

    @RequestMapping(value = "/positions/{account}", method = RequestMethod.GET)
    public List<Position> listPositionsByAccount(@PathVariable String account) {
        return positionRepo.findByKeyAccount(account);
    }

    ////////////////////////////////
    // Trades by Date
    ////////////////////////////////

    @RequestMapping(value = "/trades/{account}", method = RequestMethod.GET)
    public List<Trade> listTradesByAccount(@PathVariable String account)  {
        
        List<TradeD> trades = tradeDRepo.findByKeyAccount(account);

        List<Trade> resultTrades = new ArrayList<>();
        for(TradeD t: trades) {
            resultTrades.add(mapAsTrade(t));
        }

        return resultTrades;
        
    }
    
    @RequestMapping(value = "/trades/{account}", method=RequestMethod.POST)
    public ResponseEntity<Trade> create(HttpServletRequest req, @RequestBody Trade trade) {
        TradeD t = mapAsTradeD(trade);
        tradeDRepo.save(t);
        return ResponseEntity.accepted().body(trade);
    }

    //////////////////////////////
    // Heloer Methods
    //////////////////////////////

    private static TradeD mapAsTradeD(Trade t) {
        TradeKey key = new TradeKey(t.getAccount(), t.getTradeId());
        TradeD trade = new TradeD();
        trade.setKey(key);
        trade.setAmount(t.getAmount());
        trade.setPrice(t.getPrice());
        trade.setShares(t.getShares());
        trade.setSymbol(t.getSymbol());
        trade.setType(t.getType());
        return trade;
    }

    private static Trade mapAsTrade(TradeD t) {
        Trade trade = new Trade();
        trade.setAccount(t.getKey().getAccount());
        trade.setAmount(t.getAmount());
        trade.setPrice(t.getPrice());
        trade.setShares(t.getShares());
        trade.setSymbol(t.getSymbol());
        trade.setType(t.getType());
        trade.setTradeId(t.getKey().getTradeId());
        return trade;
    }


    
}
