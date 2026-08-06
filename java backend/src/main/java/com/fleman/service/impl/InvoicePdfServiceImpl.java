package com.fleman.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fleman.entity.BookingHeader;
import com.fleman.entity.InvoiceDetail;
import com.fleman.entity.InvoiceHeader;
import com.fleman.repository.BookingHeaderRepository;
import com.fleman.repository.InvoiceDetailRepository;
import com.fleman.repository.InvoiceHeaderRepository;
import com.fleman.service.InvoicePdfService;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class InvoicePdfServiceImpl implements InvoicePdfService {

    @Autowired
    private InvoiceHeaderRepository invoiceHeaderRepository;

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    private BookingHeaderRepository bookingHeaderRepository;

    @Override
    public byte[] generateInvoice(Long invoiceId) {

        InvoiceHeader invoice = invoiceHeaderRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        BookingHeader booking = bookingHeaderRepository.findById(invoice.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        List<InvoiceDetail> lines = invoiceDetailRepository.findByInvoiceId(invoiceId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();

        try {

            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
            Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            Paragraph title = new Paragraph("WanderCar Rental Services", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            Paragraph invoiceTitle = new Paragraph("RETURN INVOICE", headingFont);
            invoiceTitle.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(invoiceTitle);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("==================================================="));

            document.add(new Paragraph("Customer Details", headingFont));
            document.add(new Paragraph("Name              : " + invoice.getCustomerName(), normalFont));
            document.add(new Paragraph("Email             : " + invoice.getEmail(), normalFont));
            document.add(new Paragraph("Phone             : " + invoice.getPhone(), normalFont));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Booking Details", headingFont));

            document.add(new Paragraph("Confirmation No   : " + booking.getConfirmationNo(), normalFont));
            document.add(new Paragraph("Vehicle           : " + invoice.getVehicleNumber(), normalFont));
            document.add(new Paragraph("Handed over       : " + invoice.getHandoverDatetime(), normalFont));
            document.add(new Paragraph("Returned          : " + invoice.getReturnDatetime(), normalFont));
            document.add(new Paragraph("Duration          : " + invoice.getDays()
                    + (invoice.getDays() != null && invoice.getDays() == 1 ? " day" : " days"), normalFont));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("==================================================="));

            document.add(new Paragraph("Charges", headingFont));
            document.add(new Paragraph("Rental Amount     : ₹ " + invoice.getRentalAmount(), normalFont));
            for (InvoiceDetail line : lines) {
                document.add(new Paragraph(
                        String.format("  %-16s: %d x ₹ %.2f = ₹ %.2f",
                                line.getAddonName(), line.getQuantity(), line.getAddonRate(), line.getSubtotal()),
                        normalFont));
            }
            document.add(new Paragraph("Add-on Amount     : ₹ " + invoice.getAddonAmount(), normalFont));
            if (invoice.getFuelCharge() != null && invoice.getFuelCharge() > 0) {
                document.add(new Paragraph(String.format(
                        "Fuel Charge       : ₹ %.2f (returned at %d%%, handed over at %d%%)",
                        invoice.getFuelCharge(), invoice.getReturnFuelLevel(), invoice.getHandoverFuelLevel()),
                        normalFont));
            }
            if (invoice.getExtraChargeAmount() != null && invoice.getExtraChargeAmount() > 0) {
                document.add(new Paragraph("Extra Charges     : ₹ " + invoice.getExtraChargeAmount()
                        + (invoice.getExtraMiles() != null && invoice.getExtraMiles() > 0
                                ? " (" + invoice.getExtraMiles() + " extra km)" : ""), normalFont));
            }
            if (invoice.getDamageNotes() != null && !invoice.getDamageNotes().isBlank()) {
                document.add(new Paragraph("Damage Notes      : " + invoice.getDamageNotes(), normalFont));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("==================================================="));

            document.add(new Paragraph("Payment Summary", headingFont));
            document.add(new Paragraph("Total Amount      : ₹ " + invoice.getTotalAmount(), normalFont));
            document.add(new Paragraph("Payment Type      : "
                    + (invoice.getPaymentType() != null ? invoice.getPaymentType() : "Not collected"), normalFont));
            document.add(new Paragraph("Payment Status    : " + invoice.getPaymentStatus(), normalFont));
            document.add(new Paragraph("Payment Reference : " + invoice.getPaymentReference(), normalFont));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("==================================================="));

            Paragraph thanks = new Paragraph(
                    "Thank you for choosing WanderCar.\nWe wish you a safe and pleasant journey!",
                    headingFont);
            thanks.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(thanks);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return out.toByteArray();
    }
}
