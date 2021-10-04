package com.example.dubbocommon.account.api;

import com.example.dubbocommon.account.dto.AccountDTO;
import com.example.dubbocommon.account.entity.Account;
import org.dromara.hmily.annotation.Hmily;


public interface AccountService {

    Account queryAccount(Long userId, Integer accountType);

    @Hmily
    boolean decrAccount(AccountDTO accountDTO);

    /**
     * 增加余额正常情况
     */
    @Hmily
    boolean incrAccount(AccountDTO accountDTO);

    /**
     * 增加余额异常情况
     */
    @Hmily
    boolean incrAccountException(AccountDTO accountDTO);
}
