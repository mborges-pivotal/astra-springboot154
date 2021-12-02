package com.datastax.da.astra.investment.backend.repository;

import java.util.List;

import com.datastax.da.astra.investment.backend.model.Position;
import com.datastax.da.astra.investment.backend.model.PositionKey;

import org.springframework.data.repository.CrudRepository;

public interface PositionRepository extends CrudRepository<Position, PositionKey> {

    List<Position> findByKeyAccount(String account);
    
}
