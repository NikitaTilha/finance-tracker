package com.example.finance_tracker.repository;

import com.example.finance_tracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByUser_IdOrderByOccurredAtDescIdDesc(Long userId);

    Optional<Transaction> findByIdAndUser_Id(Long id, Long userId);

    @Query("""
            select coalesce(sum(t.amountCents), 0)
            from Transaction t
            where t.user.id = :userId
              and t.currency = :currency
            """)
    Long sumAmountCentsByUserIdAndCurrency(Long userId, String currency);
}