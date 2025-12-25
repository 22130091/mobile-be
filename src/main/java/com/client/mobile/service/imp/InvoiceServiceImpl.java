package com.client.mobile.service.imp;

import com.client.mobile.entity.Invoice;
import com.client.mobile.repository.InvoiceRepository;
import com.client.mobile.service.InvoiceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Invoice create(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public Invoice update(Long id, Invoice invoice) {
        return invoiceRepository.findById(id)
                .map(existing -> {
                    existing.setFinalAmount(invoice.getFinalAmount());
                    existing.setTotalAmount(invoice.getTotalAmount());
                    existing.setDiscount(invoice.getDiscount());
                    existing.setPaymentMethod(invoice.getPaymentMethod());
                    existing.setStatus(invoice.getStatus());
                    return invoiceRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public void delete(Long id) {
        invoiceRepository.deleteById(id);
    }
}
