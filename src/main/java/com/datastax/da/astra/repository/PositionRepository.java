package com.datastax.da.astra.repository;

import java.util.List;

import com.datastax.da.astra.model.Position;
import com.datastax.da.astra.model.PositionKey;

import org.springframework.data.repository.CrudRepository;

public interface PositionRepository extends CrudRepository<Position, PositionKey> {

    List<Position> findByKeyAccount(String account);
    
}
