package com.smartbank.repository;

import com.smartbank.entity.Transaction;
import com.smartbank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount(Account account);
    List<Transaction> findByAccountOrderByTransactionDateDesc(Account account);
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
    Boolean existsByReferenceNumber(String referenceNumber);
}
