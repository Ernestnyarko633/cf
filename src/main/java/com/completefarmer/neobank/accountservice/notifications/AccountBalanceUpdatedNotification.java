package com.completefarmer.neobank.accountservice.notifications;

import com.completefarmer.neobank.accountservice.account.AccountEntity;
import com.completefarmer.neobank.accountservice.enums.ENotificationChannels;
import com.completefarmer.neobank.accountservice.notifications.channels.SMSChannel;
import com.completefarmer.neobank.accountservice.transaction.TransactionEntity;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AccountBalanceUpdatedNotification implements SuccessfulTransactionNotification {
    private AccountEntity account;
    private TransactionEntity transaction;
    private final SMSChannel smsChannel;

    public AccountBalanceUpdatedNotification(SMSChannel smsChannel) {
        this.smsChannel = smsChannel;
    }

    @Override
    public String getTextMessage() {
        if (transaction.isCollection())
            return "Payment received for GHS " + transaction.getAmountInMajorUnits() + " from " + transaction.getInitiatorName()
                    + ". Available Balance: GHS " + account.getAvailableBalanceInMajorUnits() + ". Narration: "
                    + transaction.getNarration() + ". Transaction ID: " + transaction.getClientReference()
                    + ". Transaction fee: GHS 0.00";

        if (transaction.isDisbursement())
            return "You have successfully sent GHS " + transaction.getAmountInMajorUnits() + " to " + transaction.getAccountName() + " (" + transaction.getAccountNumber() + ")."
                    + " Available balance: GHS " + account.getAvailableBalanceInMajorUnits() + ". Narration: " + transaction.getNarration() + ". Transaction ID: "
                    + transaction.getClientReference() + ". Transaction fee: GHS 0.00";

        return null;
    }

    @Override
    public String getEmailBody() {
        return null;
    }

    @Override
    public void send() {
        if (account.getNotificationChannel().equals(ENotificationChannels.SMS)) {
            smsChannel.send(account, this);
        } else {

        }
    }
}
