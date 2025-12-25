package com.client.mobile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.client.mobile.entity.Invoice;
import com.client.mobile.service.InvoiceService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // ✅ Tạo hóa đơn mới
    @PostMapping
    public ResponseEntity<Invoice> create(@RequestBody Invoice invoice) {
        Invoice created = invoiceService.create(invoice);
        return ResponseEntity
                .created(URI.create("/api/invoices/" + created.getInvoiceId()))
                .body(created);
    }

    // ✅ Lấy danh sách hóa đơn
    @GetMapping
    public List<Invoice> list() {
        return invoiceService.findAll();
    }

    // ✅ Lấy 1 hóa đơn theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> get(@PathVariable Long id) {
        return invoiceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Cập nhật hóa đơn
    @PutMapping("/{id}")
    public ResponseEntity<Invoice> update(@PathVariable Long id, @RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceService.update(id, invoice));
    }

    // ✅ Xóa hóa đơn
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
