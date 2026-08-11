/**
 * 
 */
package com.fleman.service;

import com.fleman.dto.InvoicePdfRequest;

/**
 *
 */
public interface InvoicePdfService {
    // invoiceId, not bookingId — the PDF reflects the actual generated
    // invoice (handover/return time, rental/addon/extra breakdown, payment
    // status) rather than re-deriving numbers from the booking.
    byte[] generateInvoice(Long invoiceId);

    // Stateless variant for cross-service callers (see InvoiceController) —
    // builds the PDF purely from the given data, no repository lookups.
    byte[] generateInvoice(InvoicePdfRequest request);
}

