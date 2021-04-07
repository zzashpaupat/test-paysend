package org.interview.paysend.service;

import org.interview.paysend.dao.AccountDAO;
import org.interview.paysend.dto.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final AccountDAO accountDAO;

    @Autowired
    public RegistrationService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public RegistrationResult registerAccount(Credentials credentials) {
        if (accountDAO.insert(credentials.login, credentials.password) == 0) {
            return RegistrationResult.UserExists;
        }
        return RegistrationResult.Success;
    }

    public enum RegistrationResult {
        Success, UserExists
    }
}
