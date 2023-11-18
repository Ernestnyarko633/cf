package com.completefarmer.neobank.accountservice.account;

import com.completefarmer.neobank.accountservice.accounttransaction.CustomAccountTransactionRepository;
import com.completefarmer.neobank.accountservice.enums.AccountType;
import com.completefarmer.neobank.accountservice.notifications.SuccessfulTransactionNotification;
import com.completefarmer.neobank.accountservice.transaction.TransactionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation Of AccountRepository Interface
 * This class implements account repository methods
 */

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomAccountTransactionRepository customAccountTransactionRepository;
    private final AmqpAdmin amqpAdmin;
    private final SuccessfulTransactionNotification accountBalanceUpdatedNotification;

    public AccountService(
            AccountRepository accountRepository,
            CustomAccountTransactionRepository customAccountTransactionRepository,
            AmqpAdmin amqpAdmin,
            SuccessfulTransactionNotification accountBalanceUpdatedNotification) {
        this.accountRepository = accountRepository;
        this.customAccountTransactionRepository = customAccountTransactionRepository;
        this.amqpAdmin = amqpAdmin;
        this.accountBalanceUpdatedNotification = accountBalanceUpdatedNotification;
    }

    public void bindToQueue(String queueName, String routingKey, DirectExchange exchange) {
        Queue queue = new Queue(queueName);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(binding);
    }

    /**
     * This method fetches all account repositories
     *
     * @return List of AccountEntities
     */
    public List<AccountEntity> findAll() {
        return accountRepository.findAll();
    }

    /**
     * This method handles incoming http request to create new account
     *
     * @param request CreateAccountRequest as argument
     * @return AccountEntity
     */

    public AccountEntity create(CreateAccountRequest request) {
        AccountEntity account = new AccountEntity(
                request.name,
                request.type,
                request.phoneNumber,
                request.email
        );
        account = accountRepository.save(account);
        account.setIssuerId(generateIssuerId(account));
        account = accountRepository.save(account);
        return account;
    }

    /**
     * Method to generate new account issuer ID [Neobank account number]
     * Uses account phone number if the account type is CUSTOMER
     *
     * @param account Takes AccountEntity
     * @return generate Neobank Account number
     */
    private String generateIssuerId(AccountEntity account) {
        return (account.isCustomer()) ?
                account.getPhoneNumber() :
                getIssuerIdPrefix(account) + String.format("%09d", account.getId());
    }

    /**
     * Determines the prefix of the Neobank account number to be generated
     * '' => customer account
     * 1 => merchant account
     *
     * @param account AccountEntity
     * @return Neobank Account Number prefix: 1 if Account type is MERCHANT
     */

    private String getIssuerIdPrefix(AccountEntity account) {
        return account.getType().equalsIgnoreCase(AccountType.CUSTOMER.toString()) ? "" : "1";
    }

    /**
     * Find account by email address
     *
     * @param email: Neobank account holder's email
     * @return AccountEntity if email matches a record
     */

    public Optional<AccountEntity> existsByEmail(String email) {
        return accountRepository.findAccountEntityByEmail(email);
    }

    /**
     * Find account by phone number
     *
     * @param phoneNumber: Neobank account holder's phone number
     * @return AccountEntity if phone number matches a record
     */
    public Optional<AccountEntity> findByPhoneNumber(String phoneNumber) {
        return accountRepository.findByPhoneNumber(phoneNumber);
    }

    /**
     * Find account by Neobank IssuerID which dobbles as the account number used for performing transactions
     *
     * @param issuerId: Neobank account issuer ID
     * @return AccountEntity if id matches a record
     */
    public Optional<AccountEntity> findByIssuerId(String issuerId) {
        return accountRepository.findByIssuerId(issuerId);
    }

    /**
     * Get accounts by type
     *
     * @param type: Neobank account type: CUSTOMER, MERCHANT
     * @return List AccountEntities the match the specified type
     */
    public List<AccountEntity> filterByType(String type) {
        return accountRepository.findByType(type);
    }

    /**
     * Updates Neobank Account details
     *
     * @param account: Neobank account
     * @return void
     */
    public AccountEntity update(AccountEntity account, Long availableBalance) {
        account = accountRepository.save(account);
        if (!Objects.equals(availableBalance, account.getAvailableBalance())) {
            log.info("Account balance updated");
            notify(account);
        }
        return account;
    }

    public void notify(AccountEntity account) {
        Optional<TransactionEntity> transaction = customAccountTransactionRepository.getLatestSuccessfulTransaction(account);
        transaction.ifPresent(t -> {
            accountBalanceUpdatedNotification.setAccount(account);
            accountBalanceUpdatedNotification.setTransaction(t);
            accountBalanceUpdatedNotification.send();
        });
    }
}
