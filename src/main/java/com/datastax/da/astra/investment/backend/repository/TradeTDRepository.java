package com.datastax.da.astra.investment.backend.repository;

import java.util.List;

import com.datastax.da.astra.investment.backend.model.trade.TradeTD;
import com.datastax.da.astra.investment.backend.model.trade.TradeTypeKey;

import org.springframework.data.repository.CrudRepository;

public interface TradeTDRepository extends CrudRepository<TradeTD, TradeTypeKey> {

    List<TradeTD> findByKeyAccount(String account);

    List<TradeTD> findByKeyAccountAndKeyType(String account, String type);

}
