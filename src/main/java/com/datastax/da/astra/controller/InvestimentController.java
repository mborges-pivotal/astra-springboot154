package com.datastax.da.astra.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.datastax.da.astra.model.Account;
import com.datastax.da.astra.model.Position;
import com.datastax.da.astra.model.Trade;
import com.datastax.da.astra.model.trade.TradeD;
import com.datastax.da.astra.model.trade.TradeSD;
import com.datastax.da.astra.model.trade.TradeSymbolKey;
import com.datastax.da.astra.model.trade.TradeTD;
import com.datastax.da.astra.repository.AccountRepository;
import com.datastax.da.astra.repository.PositionRepository;
import com.datastax.da.astra.repository.TradeDRepository;
import com.datastax.da.astra.repository.TradeSDRepository;
import com.datastax.da.astra.repository.TradeTDRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.datastax.da.astra.model.trade.TradeUtilities.mapAsTrade;
import static com.datastax.da.astra.model.trade.TradeUtilities.mapAsTradeD;
import static com.datastax.da.astra.model.trade.TradeUtilities.mapAsTradeSD;
import static com.datastax.da.astra.model.trade.TradeUtilities.mapAsTradeTD;

@RestController
public class InvestimentController {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private PositionRepository positionRepo;

    @Autowired
    private TradeDRepository tradeDRepo;

    @Autowired
    private TradeSDRepository tradeSDRepo;

    @Autowired
    private TradeTDRepository tradeTDRepo;

    @RequestMapping(value = "/accounts/{username}", method = RequestMethod.GET)
    public List<Account> listAccounts(@PathVariable("username") String userName) {
        return accountRepo.findByKeyUserName(userName);
    }

    @RequestMapping(value = "/positions/{account}", method = RequestMethod.GET)
    public List<Position> listPositionsByAccount(@PathVariable String account) {
        return positionRepo.findByKeyAccount(account);
    }

    ////////////////////////////////
    // Trades
    ////////////////////////////////

    @RequestMapping(value = "/trades/{account}", method = RequestMethod.GET)
    public List<Trade> listTradesByAccount(@PathVariable String account, @RequestParam(required = false) String symbol,
            @RequestParam(required = false) String type) {

        // passed symbol
        if (symbol != null) {
            List<TradeSD> trades = tradeSDRepo.findByKeyAccountAndKeySymbol(account, symbol);
            return mapAsTradeSD(trades);
        }

        // passed type
        if (type != null) {
            List<TradeTD> trades = tradeTDRepo.findByKeyAccountAndKeyType(account, type);
            return mapAsTradeTD(trades);
        }

        // Just account
        List<TradeD> trades = tradeDRepo.findByKeyAccount(account);
        return mapAsTradeD(trades);

    }

    // CREATE A TRADE

    @RequestMapping(value = "/trades/{account}", method = RequestMethod.POST)
    public ResponseEntity<Trade> create(HttpServletRequest req, @RequestBody Trade trade) {

        // MMB - Review the best way to ingest data here.
        tradeDRepo.save(mapAsTradeD(trade));
        tradeSDRepo.save(mapAsTradeSD(trade));
        tradeTDRepo.save(mapAsTradeTD(trade));

        return ResponseEntity.accepted().body(trade);
    }

}
