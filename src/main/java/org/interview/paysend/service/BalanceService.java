package org.interview.paysend.service;

import org.interview.paysend.dao.AccountDAO;
import org.interview.paysend.dto.Credentials;
import org.interview.paysend.tables.records.AccountRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BalanceService {
    private final AccountDAO accountDAO;

    @Autowired
    public BalanceService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public BalanceRequestResult getBalance(Credentials credentials) {
        Optional<AccountRecord> accountByLogin = accountDAO.getAccountByLogin(credentials.login);
        if (accountByLogin.isEmpty()) {
            return BalanceRequestResult.NO_SUCH_ACCOUNT;
        }
        AccountRecord accountRecord = accountByLogin.get();
        String accountPassword = accountRecord.getPassword();
        if (!accountPassword.equals(credentials.password)) {
            return BalanceRequestResult.WRONG_PASSWORD;
        }
        return BalanceRequestResult.success(accountRecord.getBalance());
    }

    public static class BalanceRequestResult {
        public final Result result;
        public final BigDecimal balance;

        public static final BalanceRequestResult NO_SUCH_ACCOUNT = new BalanceRequestResult(Result.NoSuchAccount, null);
        public static final BalanceRequestResult WRONG_PASSWORD = new BalanceRequestResult(Result.WrongPassword, null);

        public static BalanceRequestResult success(BigDecimal balance) {
            return new BalanceRequestResult(Result.Success, balance);
        }

        private BalanceRequestResult(Result result, BigDecimal balance) {
            this.result = result;
            this.balance = balance;
        }
    }

    public enum Result {
        NoSuchAccount, WrongPassword, Success
    }
}
