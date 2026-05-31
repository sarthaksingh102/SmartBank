package com.smartbank.repository;

import com.smartbank.entity.BillPayment;
import com.smartbank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {
    List<BillPayment> findByAccount(Account account);
    Optional<BillPayment> findByReferenceNumber(String referenceNumber);
}
