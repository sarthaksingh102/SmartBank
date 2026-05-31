package com.smartbank.repository;

import com.smartbank.entity.QRPayment;
import com.smartbank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QRPaymentRepository extends JpaRepository<QRPayment, Long> {
    Optional<QRPayment> findByQrCode(String qrCode);
    List<QRPayment> findByAccount(Account account);
}
