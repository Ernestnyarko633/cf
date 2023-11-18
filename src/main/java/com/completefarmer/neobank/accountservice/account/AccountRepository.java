package com.completefarmer.neobank.accountservice.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Interface for Account Entity
 * Contains methods for querying Account Entity Data from DB
 * @author appiersign
 */
@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    Optional<AccountEntity> findAccountEntityByEmail(String email);

    Optional<AccountEntity> findByPhoneNumber(String phoneNumber);

    Optional<AccountEntity> findByIssuerId(String issuerId);

    List<AccountEntity> findByType(String type);
}
