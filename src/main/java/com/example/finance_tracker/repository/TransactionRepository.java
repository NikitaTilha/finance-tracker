package com.example.finance_tracker.repository;

import com.example.finance_tracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndUser_Id(Long id, Long userId);

    List<Transaction> findAllByUser_IdOrderByOccurredAtDescIdDesc(Long userId);

    @Query("""
            select t
            from Transaction t
            where t.user.id = :userId
              and (:categoryId is null or t.category.id = :categoryId)
              and (:currency is null or t.currency = :currency)
              and (:dateFrom is null or t.occurredAt >= :dateFrom)
              and (:dateTo is null or t.occurredAt <= :dateTo)
            order by t.occurredAt desc, t.id desc
            """)
    List<Transaction> findAllByFilters(
            Long userId,
            Long categoryId,
            String currency,
            OffsetDateTime dateFrom,
            OffsetDateTime dateTo
    );

    @Query("""
            select coalesce(sum(
                case
                    when t.category.type = 'INCOME' then t.amountCents
                    when t.category.type = 'EXPENSE' then -t.amountCents
                    else 0
                end
            ), 0)
            from Transaction t
            where t.user.id = :userId
              and t.currency = :currency
            """)
    Long sumAmountCentsByUserIdAndCurrency(Long userId, String currency);
}