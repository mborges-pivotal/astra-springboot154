package com.datastax.da.astra.model.trade;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class TradeSymbolKey implements Serializable {

    @PrimaryKeyColumn(name = "account", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String account;

    @PrimaryKeyColumn(name = "symbol", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private String symbol;

    @PrimaryKeyColumn(name = "trade_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private UUID tradeId;
    
}
