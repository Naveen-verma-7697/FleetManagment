/**
 * 
 */
package com.fleman.service;

/**
 * 
 */
public interface InvoicePdfService {
    // invoiceId, not bookingId — the PDF reflects the actual generated
    // invoice (handover/return time, rental/addon/extra breakdown, payment
    // status) rather than re-deriving numbers from the booking.
    byte[] generateInvoice(Long invoiceId);
}

