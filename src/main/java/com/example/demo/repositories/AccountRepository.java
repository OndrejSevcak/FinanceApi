package com.example.demo.repositories;

import com.example.demo.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_account (user_key, acc_type_key, acc_key) VALUES (:userKey, :accTypeKey, :accKey)", nativeQuery = true)
    void insertUserAccount(@Param("userKey") Long userKey, @Param("accTypeKey") String accTypeKey, @Param("accKey") Long accKey);

    @Query(value = "SELECT COUNT(*) FROM user_account WHERE user_key = :userKey AND acc_type_key = :accTypeKey AND acc_key = :accKey", nativeQuery = true)
    int countUserAccount(@Param("userKey") Long userKey, @Param("accTypeKey") String accTypeKey, @Param("accKey") Long accKey);

    @Query(value = "SELECT COUNT(*) FROM user_account WHERE user_key = :userKey AND acc_type_key = :accTypeKey", nativeQuery = true)
    int countUserAccountsByType(@Param("userKey") Long userKey, @Param("accTypeKey") String accTypeKey);

    @Query(value = "SELECT COUNT(*) FROM user_account ua JOIN account a ON ua.acc_key = a.acc_key WHERE ua.user_key = :userKey AND ua.acc_type_key = 'CRY' AND UPPER(TRIM(a.currency_code)) = UPPER(TRIM(:currencyCode))", nativeQuery = true)
    int countUserCryptoByCurrency(@Param("userKey") Long userKey, @Param("currencyCode") String currencyCode);

    // atomic debit: subtract amount from balance only if sufficient
    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET balance = balance - :amount WHERE acc_key = :accKey AND balance >= :amount", nativeQuery = true)
    int debitIfSufficient(@Param("accKey") Long accKey, @Param("amount") BigDecimal amount);

    // credit: add amount to balance
    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET balance = balance + :amount WHERE acc_key = :accKey", nativeQuery = true)
    int credit(@Param("accKey") Long accKey, @Param("amount") BigDecimal amount);

    // get balance for an account (native query to lock in same transaction if needed)
    @Query(value = "SELECT balance FROM account WHERE acc_key = :accKey", nativeQuery = true)
    BigDecimal getBalance(@Param("accKey") Long accKey);

}
