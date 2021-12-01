package com.datastax.da.astra;

import com.datastax.da.astra.model.Account;
import com.datastax.da.astra.model.AccountKey;
import com.datastax.da.astra.model.Position;
import com.datastax.da.astra.model.PositionKey;
import com.datastax.da.astra.model.trade.TradeD;
import com.datastax.da.astra.model.trade.TradeKey;
import com.datastax.da.astra.repository.AccountRepository;
import com.datastax.da.astra.repository.PositionRepository;
import com.datastax.da.astra.repository.TradeDRepository;
import com.datastax.driver.core.utils.UUIDs;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
// It was not taking the secure bundle contact points
// @EnableAutoConfiguration(exclude = { CassandraDataAutoConfiguration.class })
public class AstraApplication implements CommandLineRunner {

	/** Logger for the class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AstraApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AstraApplication.class, args);
	}

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private PositionRepository positionRepo;

	@Autowired
	private TradeDRepository tradeDRepo;

	@Override
	public void run(String... arg0) throws Exception {

		AccountKey ak1 = new AccountKey("mborges", "002");
		Account a1 = new Account(ak1, BigDecimal.ZERO, "Marcelo Borges");

		Account ar1 = accountRepo.findOne(ak1);
		if (ar1 != null) {
			LOGGER.info("Account key {} found", ar1);
		} else {
			ar1 = accountRepo.save(a1);
			LOGGER.info("Saved account {}", ar1);
		}

		PositionKey pk1 = new PositionKey("001", "SNAP");
		Position p1 = new Position(pk1, BigDecimal.valueOf(50));

		Position pr1 = positionRepo.findOne(pk1);
		if (pr1 != null) {
			LOGGER.info("Position key {} found", pr1);
		} else {
			pr1 = positionRepo.save(p1);
			LOGGER.info("Saved position {}", pr1);
		}

		TradeKey tk1 = new TradeKey("001",  UUIDs.timeBased());
		TradeD t1 = new TradeD();
		t1.setKey(tk1);
		t1.setPrice(BigDecimal.valueOf(144L));
		t1.setShares(BigDecimal.TEN);
		t1.setAmount(t1.getShares().multiply(t1.getPrice()));
		t1.setSymbol("SNAP");
		t1.setType("buy");
		tradeDRepo.save(t1);


	}

}
