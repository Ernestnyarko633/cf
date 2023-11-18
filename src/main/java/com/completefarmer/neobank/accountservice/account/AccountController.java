package com.completefarmer.neobank.accountservice.account;

import com.completefarmer.neobank.accountservice.accounttransaction.AccountTransactionRepository;
import com.completefarmer.neobank.accountservice.accounttransaction.CustomAccountTransactionRepository;
import com.completefarmer.neobank.accountservice.utils.responses.JsonResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Rest Controller for Account endpoints
 * @author appiersign
 */

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDTOMapper accountDTOMapper;

    @Autowired
    private CustomAccountTransactionRepository customAccountTransactionRepository;

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    /**
     * Endpoint to fetch all accounts and filter by type{CUSTOMER, MERCHANT}
     * @return ResponseEntity
     */
    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(required = false) String type) {
        List<AccountEntity> query = type == null ? accountService.findAll() : accountService.filterByType(type);
        List<AccountDTO> accounts = query.stream()
                .map(accountDTOMapper)
                .collect(Collectors.toList());
        return JsonResponse.set(HttpStatus.OK, "Accounts data fetched.", accounts);
    }

    /**
     * Endpoint to CREATE new accounts
     * @param request
     * @return ResponseEntity
     */
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody @Valid CreateAccountRequest request) {
        AccountEntity account = accountService.create(request);
        return JsonResponse.set(HttpStatus.CREATED, "Account created.", accountDTOMapper.apply(account));
    }

    /**
     * Endpoint to VERIFY accounts
     * @param issuerId
     * @return ResponseEntity
     */
    @GetMapping("/{issuerId}/verify")
    public ResponseEntity<?> verifyAccount(@PathVariable String issuerId) {
        Optional<AccountEntity> account = accountService.findByIssuerId(issuerId);

        Map<String, String> data = new HashMap<>();

        account.ifPresent(a -> {
            data.put("accountName", a.getName());
            data.put("accountNumber", a.getIssuerId());
        });

        return JsonResponse.set(
                account.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK,
                account.isEmpty() ? "Account not found" : "Account verification successful",
                data
        );
    }

    /**
     * Endpoint to CHECK account balance
     * @param issuerId
     * @return ResponseEntity
     */
    @GetMapping("/{issuerId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String issuerId) {
        Optional<AccountEntity> account = accountService.findByIssuerId(issuerId);

        Map<String, String> data = new HashMap<>();

        account.ifPresent(a -> {
            data.put("actualBalance", a.getActualBalance() + "");
            data.put("availableBalance", a.getAvailableBalance() + "");
        });

        return JsonResponse.set(
                account.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK,
                account.isEmpty() ? "Account not found" : "Account balance retrieved.",
                data
        );
    }

    /**
     * Endpoint to fetch account stats
     * @param issuerId
     * @return ResponseEntity
     */
    @GetMapping("/{issuerId}/stats")
    public ResponseEntity<?> getStats(@PathVariable String issuerId) {
        Optional<AccountEntity> account = accountService.findByIssuerId(issuerId);

        Map<String, Object> data = new HashMap<>();

        account.ifPresent(a -> {
            data.put("actualBalance", a.getActualBalance());
            data.put("availableBalance", a.getAvailableBalance());
            data.put("TransactionCount", customAccountTransactionRepository.getTotalTransactionCount(a));
            data.put("collectionCount", customAccountTransactionRepository.getTotalCollectionCount(a));
            data.put("collectionValue", customAccountTransactionRepository.getTotalCollectionValue(a));
            data.put("disbursementCount", customAccountTransactionRepository.getTotalDisbursementCount(a));
            data.put("disbursementValue", customAccountTransactionRepository.getTotalDisbursementValue(a));
            data.put("recentTransactions", accountTransactionRepository.findAllByAccountId(a.getId()));
        });

        return JsonResponse.set(
                account.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK,
                account.isEmpty() ? "Account not found" : "Account statistics retrieved.",
                data
        );
    }

}
