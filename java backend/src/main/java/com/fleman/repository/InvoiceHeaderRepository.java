package com.fleman.repository;

import com.fleman.entity.InvoiceHeader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceHeaderRepository extends JpaRepository<InvoiceHeader, Long> {
}
