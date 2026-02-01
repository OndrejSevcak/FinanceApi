package com.example.demo.repositories;

import com.example.demo.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepositoryCustom {

    // Derived query to find first transaction by idempotency key (entity)
    // spring data will split method name and generate query accordingly
    Optional<Transaction> findFirstByIdempotencyKey(String idempotencyKey);

    // JPQL query to efficiently return only txKey for a given idempotency key (latest)
    @Query("SELECT t.txKey FROM Transaction t WHERE t.idempotencyKey = :key ORDER BY t.txKey DESC")
    Optional<Long> findFirstTxKeyByIdempotencyKey(@Param("key") String idempotencyKey);

    // Use JPQL over the Account entity to get currency code for an account key
    @Query("SELECT a.currencyCode FROM Account a WHERE a.accKey = :accKey")
    Optional<String> findCurrencyCodeByAccKey(@Param("accKey") Long accKey);
}
