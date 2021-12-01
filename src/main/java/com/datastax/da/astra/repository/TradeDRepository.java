package com.datastax.da.astra.repository;

import java.util.List;

import com.datastax.da.astra.model.trade.TradeD;
import com.datastax.da.astra.model.trade.TradeKey;

import org.springframework.data.repository.CrudRepository;

public interface TradeDRepository extends CrudRepository<TradeD, TradeKey> {

    List<TradeD> findByKeyAccount(String account);

}
