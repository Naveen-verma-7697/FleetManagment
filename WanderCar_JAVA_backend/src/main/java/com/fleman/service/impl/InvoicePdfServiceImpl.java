package com.fleman.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fleman.dto.InvoiceLineItemDTO;
import com.fleman.dto.InvoicePdfRequest;
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

        return buildPdf(
                invoice.getCustomerName(),
                invoice.getEmail(),
                invoice.getPhone(),
                booking.getConfirmationNo(),
                invoice.getVehicleNumber(),
                invoice.getHandoverDatetime(),
                invoice.getReturnDatetime(),
                invoice.getDays(),
                invoice.getRentalAmount(),
                lines.stream().map(line -> new LineItem(
                        line.getAddonName(), line.getQuantity(), line.getAddonRate(), line.getSubtotal()
                )).collect(Collectors.toList()),
                invoice.getAddonAmount(),
                invoice.getFuelCharge(),
                invoice.getReturnFuelLevel(),
                invoice.getHandoverFuelLevel(),
                invoice.getExtraChargeAmount(),
                invoice.getExtraMiles(),
                invoice.getDamageNotes(),
                invoice.getTotalAmount(),
                invoice.getPaymentType() != null ? invoice.getPaymentType().name() : null,
                invoice.getPaymentStatus() != null ? invoice.getPaymentStatus().name() : null,
                invoice.getPaymentReference()
        );
    }

    @Override
    public byte[] generateInvoice(InvoicePdfRequest request) {

        List<LineItem> lines = request.getLines() == null
                ? List.of()
                : request.getLines().stream()
                        .map(l -> new LineItem(l.getAddonName(), l.getQuantity(), l.getAddonRate(), l.getSubtotal()))
                        .collect(Collectors.toList());

        return buildPdf(
                request.getCustomerName(),
                request.getEmail(),
                request.getPhone(),
                request.getConfirmationNo(),
                request.getVehicleNumber(),
                request.getHandoverDatetime(),
                request.getReturnDatetime(),
                request.getDays(),
                request.getRentalAmount(),
                lines,
                request.getAddonAmount(),
                request.getFuelCharge(),
                request.getReturnFuelLevel(),
                request.getHandoverFuelLevel(),
                request.getExtraChargeAmount(),
                request.getExtraMiles(),
                request.getDamageNotes(),
                request.getTotalAmount(),
                request.getPaymentType(),
                request.getPaymentStatus(),
                request.getPaymentReference()
        );
    }

    private record LineItem(String addonName, Integer quantity, Double addonRate, Double subtotal) {}

    private byte[] buildPdf(
            String customerName,
            String email,
            String phone,
            String confirmationNo,
            String vehicleNumber,
            LocalDateTime handoverDatetime,
            LocalDateTime returnDatetime,
            Integer days,
            Double rentalAmount,
            List<LineItem> lines,
            Double addonAmount,
            Double fuelCharge,
            Integer returnFuelLevel,
            Integer handoverFuelLevel,
            Double extraChargeAmount,
            Integer extraMiles,
            String damageNotes,
            Double totalAmount,
            String paymentType,
            String paymentStatus,
            String paymentReference) {

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
            document.add(new Paragraph("Name              : " + customerName, normalFont));
            document.add(new Paragraph("Email             : " + email, normalFont));
            document.add(new Paragraph("Phone             : " + phone, normalFont));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Booking Details", headingFont));

            document.add(new Paragraph("Confirmation No   : " + confirmationNo, normalFont));
            document.add(new Paragraph("Vehicle           : " + vehicleNumber, normalFont));
            document.add(new Paragraph("Handed over       : " + handoverDatetime, normalFont));
            document.add(new Paragraph("Returned          : " + returnDatetime, normalFont));
            document.add(new Paragraph("Duration          : " + days
                    + (days != null && days == 1 ? " day" : " days"), normalFont));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("==================================================="));

            document.add(new Paragraph("Charges", headingFont));
            document.add(new Paragraph("Rental Amount     : ₹ " + rentalAmount, normalFont));
            for (LineItem line : lines) {
                document.add(new Paragraph(
                        String.format("  %-16s: %d x ₹ %.2f = ₹ %.2f",
                                line.addonName(), line.quantity(), line.addonRate(), line.subtotal()),
                        normalFont));
            }
            document.add(new Paragraph("Add-on Amount     : ₹ " + addonAmount, normalFont));
            if (fuelCharge != null && fuelCharge > 0) {
                document.add(new Paragraph(String.format(
                        "Fuel Charge       : ₹ %.2f (returned at %d%%, handed over at %d%%)",
                        fuelCharge, returnFuelLevel, handoverFuelLevel),
                        normalFont));
            }
            if (extraChargeAmount != null && extraChargeAmount > 0) {
                document.add(new Paragraph("Extra Charges     : ₹ " + extraChargeAmount
                        + (extraMiles != null && extraMiles > 0
                                ? " (" + extraMiles + " extra km)" : ""), normalFont));
            }
            if (damageNotes != null && !damageNotes.isBlank()) {
                document.add(new Paragraph("Damage Notes      : " + damageNotes, normalFont));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("==================================================="));

            document.add(new Paragraph("Payment Summary", headingFont));
            document.add(new Paragraph("Total Amount      : ₹ " + totalAmount, normalFont));
            document.add(new Paragraph("Payment Type      : "
                    + (paymentType != null ? paymentType : "Not collected"), normalFont));
            document.add(new Paragraph("Payment Status    : " + paymentStatus, normalFont));
            document.add(new Paragraph("Payment Reference : " + paymentReference, normalFont));

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
