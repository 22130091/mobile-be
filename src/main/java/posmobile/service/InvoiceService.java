package posmobile.service;

import posmobile.entity.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    Invoice create(Invoice invoice);
    List<Invoice> findAll();
    Optional<Invoice> findById(Long id);
    Invoice update(Long id, Invoice invoice);
    void delete(Long id);
}
