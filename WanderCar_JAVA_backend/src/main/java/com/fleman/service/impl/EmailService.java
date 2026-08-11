/**
 * 
 */
package com.fleman.service.impl;

/**
 * 
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.scheduling.annotation.Async;
import com.fleman.entity.BookingHeader;
import com.fleman.entity.InvoiceHeader;

import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Async("emailExecutor")
    public void sendEmail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
    @Async("emailExecutor")
  public void sendBookingInvoice(String to,
            String customerName,
            BookingHeader booking,
            InvoiceHeader invoice,
            byte[] pdf) throws Exception {

MimeMessage message = mailSender.createMimeMessage();

MimeMessageHelper helper =
new MimeMessageHelper(message, true);

helper.setTo(to);
helper.setSubject("WanderCar Booking " + booking.getConfirmationNo() + " — Vehicle Returned");

boolean paid = invoice.getPaymentStatus() != null
        && "PAID".equals(invoice.getPaymentStatus().name());

helper.setText("""
Dear %s,

Your vehicle has been returned and this booking is now complete.

Booking Number    : %s
Vehicle           : %s
Handed over       : %s
Returned          : %s
Duration          : %s day(s)

Rental Amount     : ₹%.2f
Add-on Amount     : ₹%.2f
Fuel Charge       : ₹%.2f
Extra Charges     : ₹%.2f
Total Amount      : ₹%.2f
Payment Status    : %s%s

%s

Thank you for choosing WanderCar.

Invoice attached.

""".formatted(
customerName,
booking.getConfirmationNo(),
invoice.getVehicleNumber(),
invoice.getHandoverDatetime(),
invoice.getReturnDatetime(),
invoice.getDays(),
invoice.getRentalAmount(),
invoice.getAddonAmount(),
invoice.getFuelCharge() != null ? invoice.getFuelCharge() : 0.0,
invoice.getExtraChargeAmount() != null ? invoice.getExtraChargeAmount() : 0.0,
invoice.getTotalAmount(),
invoice.getPaymentStatus(),
invoice.getPaymentReference() != null ? " (" + invoice.getPaymentReference() + ")" : "",
paid ? "Payment has been received in full." : "Payment is still pending collection at this time."
));

helper.addAttachment("Invoice.pdf",
new ByteArrayResource(pdf));

mailSender.send(message);
}
}
