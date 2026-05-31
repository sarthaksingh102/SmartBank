package com.smartbank.repository;

import com.smartbank.entity.ScheduledTransaction;
import com.smartbank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledTransactionRepository extends JpaRepository<ScheduledTransaction, Long> {
    List<ScheduledTransaction> findByAccount(Account account);
    List<ScheduledTransaction> findByStatusAndScheduledDateBefore(
            ScheduledTransaction.ScheduleStatus status, LocalDateTime date);
}
