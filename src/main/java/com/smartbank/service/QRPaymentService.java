package com.smartbank.service;

import com.smartbank.entity.QRPayment;
import com.smartbank.entity.Account;
import com.smartbank.repository.QRPaymentRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class QRPaymentService {

    private final QRPaymentRepository qrPaymentRepository;

    public QRPaymentService(QRPaymentRepository qrPaymentRepository) {
        this.qrPaymentRepository = qrPaymentRepository;
    }

    public QRPayment generateQRPayment(Account account, BigDecimal amount, String description) {
        String qrCode = generateQRCode();

        QRPayment qrPayment = new QRPayment();
        qrPayment.setAccount(account);
        qrPayment.setQrCode(qrCode);
        qrPayment.setAmount(amount);
        qrPayment.setDescription(description);
        qrPayment.setStatus(QRPayment.QRStatus.ACTIVE);
        qrPayment.setExpiryDate(LocalDateTime.now().plusHours(24));

        return qrPaymentRepository.save(qrPayment);
    }

    public String generateQRCodeImage(String qrCode) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCode, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);

            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code image", e);
        }
    }

    public QRPayment getQRPaymentByCode(String qrCode) {
        return qrPaymentRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("QR Payment not found"));
    }

    public List<QRPayment> getQRPaymentsByAccount(Account account) {
        return qrPaymentRepository.findByAccount(account);
    }

    private String generateQRCode() {
        return "SB-QR-" + UUID.randomUUID().toString();
    }

}
