package com.patrick.personalfinance.application.port;

import com.patrick.personalfinance.domain.entity.User;

public interface CurrentUserProvider {

    User getCurrentUser();
}
