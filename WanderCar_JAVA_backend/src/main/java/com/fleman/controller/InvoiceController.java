package com.fleman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fleman.dto.InvoicePdfRequest;
import com.fleman.service.InvoicePdfService;

// Lets the .NET backend (FlemanApi) generate an invoice PDF via this Java
// backend over HTTP instead of generating it locally — see
// JavaMicroserviceClient.GenerateInvoicePdfAsync on the .NET side.
//
// Takes the full invoice/booking/line-item payload rather than an id: the
// two backends run against separate databases with independently
// auto-incrementing ids, so an invoiceId minted by the .NET side means
// nothing looked up against this app's own database (that was the bug —
// this endpoint used to take just an id and returned whatever unrelated
// invoice happened to have that id here).
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoicePdfService invoicePdfService;

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateInvoicePdf(@RequestBody InvoicePdfRequest request) {
        byte[] pdf = invoicePdfService.generateInvoice(request);
        String filename = request.getConfirmationNo() != null ? request.getConfirmationNo() : "invoice";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
