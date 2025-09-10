package com.patrick.personalfinance.service;

import java.util.List;
import java.util.UUID;

public interface CrudService <Req, Res>{
    List<Res> getAll();
    Res getById(UUID id);
    Res create(Req dto);
    Res update(UUID id, Req dto);
    void delete(UUID id);
}
