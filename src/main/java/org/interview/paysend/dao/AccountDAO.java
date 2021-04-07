package org.interview.paysend.dao;

import org.interview.paysend.tables.records.AccountRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

import static org.interview.paysend.Tables.ACCOUNT;

@Component
public class AccountDAO {
    private final DSLContext dslContext;

    @Autowired
    public AccountDAO(DSLContext dslContext) {
        this.dslContext = dslContext;
    }


    public int insert(String login, String password) {
        return dslContext.insertInto(ACCOUNT)
                .set(ACCOUNT.LOGIN, login)
                .set(ACCOUNT.PASSWORD, password)
                .set(ACCOUNT.BALANCE, BigDecimal.ZERO)
                .onConflictDoNothing()
                .execute();
    }

    public Optional<AccountRecord> getAccountByLogin(String login) {
        return dslContext.selectFrom(ACCOUNT)
                .where(ACCOUNT.LOGIN.eq(login))
                .fetchOptional();
    }
}
