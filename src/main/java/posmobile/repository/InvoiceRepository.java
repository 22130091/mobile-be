package posmobile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import posmobile.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
