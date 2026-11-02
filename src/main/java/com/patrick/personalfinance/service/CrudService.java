package com.patrick.personalfinance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CrudService<Req, Res> {

    Page<Res> getAll(Pageable pageable);

    Res getById(UUID id);

    Res create(Req dto);

    Res update(UUID id, Req dto);

    void delete(UUID id);
}
