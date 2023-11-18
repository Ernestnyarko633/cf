package com.completefarmer.neobank.accountservice.account;

import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * DTO Mapper for mapping Account entity properties to AccountDTO
 */
@Service
public final class AccountDTOMapper implements Function<AccountEntity, AccountDTO> {
    @Override
    public AccountDTO apply(AccountEntity account) {
        return new AccountDTO(
                account.getExternalId(),
                account.getName(),
                account.getIssuerId(),
                account.getType(),
                account.getStatus(),
                account.getPhoneNumber(),
                account.getEmail(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
