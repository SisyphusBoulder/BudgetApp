package org.beaverbrooks.domain.account;

import java.math.BigDecimal;

public interface IAccount {

    BigDecimal AddToAccount(BigDecimal amount);

    BigDecimal SubtractFromAccount(BigDecimal amount);

    BigDecimal GetAccountBalance();
}
