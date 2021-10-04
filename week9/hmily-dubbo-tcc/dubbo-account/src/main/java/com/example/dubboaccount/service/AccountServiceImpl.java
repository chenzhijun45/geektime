package com.example.dubboaccount.service;

import com.example.dubbocommon.account.api.AccountService;
import com.example.dubbocommon.account.dto.AccountDTO;
import com.example.dubbocommon.account.entity.Account;
import com.example.dubbocommon.account.mapper.AccountMapper;
import com.example.dubbocommon.exception.HmilyRuntimeException;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public Account queryAccount(Long userId, Integer accountType) {
        Account account = accountMapper.findByUserIdAndAccountType(userId, accountType);
        System.out.println("account=>" + account.toString());
        return account;
    }

    /**
     * 账户余额扣减，例如人民币兑换美元，这里是扣减人民币余额 TCC的try阶段
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @HmilyTCC(confirmMethod = "confirmDecrAmount", cancelMethod = "cancelDecrAmount")
    public boolean decrAccount(AccountDTO accountDTO) {
        int count = accountMapper.decrAmount(accountDTO);
        System.out.println("账户余额扣减 count：" + (count == 1));
        if (count != 1) {
            System.out.println("账户余额扣减异常");
            throw new HmilyRuntimeException("账户扣减异常！");
        } else {
            return true;
        }
    }


    AtomicLong confirmCount = new AtomicLong(0);

    @Transactional(rollbackFor = Exception.class)
    public boolean confirmDecrAmount(AccountDTO accountDTO) {
        System.out.println("============dubbo tcc 执行账户余额扣减确认接口===============");
        int result = accountMapper.confirmDecrAmount(accountDTO);
        System.out.println("confirmDecrAmount执行结果：" + (result == 1));
        final long count = confirmCount.incrementAndGet();
        System.out.println("账户余额扣减确认接口执行了" + count + "次");
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancelDecrAmount(AccountDTO accountDTO) {
        System.out.println("============dubbo tcc 执行账户余额扣减取消接口===============");
        int count = accountMapper.cancelDecrAmount(accountDTO);
        System.out.println("cancelDecrAmount执行结果：" + (count == 1));
        return true;
    }


    /**
     * 账户余额增加，例如人民币兑换美元，这里是增加美元余额 TCC的try阶段
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @HmilyTCC(confirmMethod = "confirmIncrAccount", cancelMethod = "cancelIncrAmount")
    public boolean incrAccount(AccountDTO accountDTO) {
        int count = accountMapper.incrAmount(accountDTO);
        System.out.println("账户余额增加 count = " + count);
        if (count != 1) {
            System.out.println("账户余额增加异常");
            throw new HmilyRuntimeException("账户增加异常！");
        } else {
            return true;
        }
    }

    /**
     * 账户余额增加异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @HmilyTCC(confirmMethod = "confirmIncrAccount", cancelMethod = "cancelIncrAmount")
    public boolean incrAccountException(AccountDTO accountDTO) {
        throw new RuntimeException("增加账户余额异常");
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean confirmIncrAccount(AccountDTO accountDTO) {
        System.out.println("============dubbo tcc 执行账户余额增加确认接口===============");
        System.out.println("accountDTO=" + accountDTO);
        int count = accountMapper.confirmIncrAmount(accountDTO);
        System.out.println("confirmIncrAccount执行结果：" + (count == 1));
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancelIncrAmount(AccountDTO accountDTO) {
        System.out.println("============dubbo tcc 执行账户余额增加取消接口===============");
        System.out.println("accountDTO=" + accountDTO);
        int count = accountMapper.cancelIncrAmount(accountDTO);
        System.out.println("cancelIncrAmount执行结果：" + (count == 1));
        return true;
    }

}
