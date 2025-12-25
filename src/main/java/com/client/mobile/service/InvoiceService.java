package com.client.mobile.service;

import com.client.mobile.entity.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    Invoice create(Invoice invoice);
    List<Invoice> findAll();
    Optional<Invoice> findById(Long id);
    Invoice update(Long id, Invoice invoice);
    void delete(Long id);
}
