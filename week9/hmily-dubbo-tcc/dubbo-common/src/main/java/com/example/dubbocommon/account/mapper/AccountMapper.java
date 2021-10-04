package com.example.dubbocommon.account.mapper;

import com.example.dubbocommon.account.dto.AccountDTO;
import com.example.dubbocommon.account.entity.Account;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface AccountMapper {

    /**
     * 扣减账户余额 TCC的try阶段
     * 可用数量减少 冻结数量增加 总数量不变
     */
    @Update("update t_account set available_amount = available_amount - #{amount}, " +
            "frozen_amount = frozen_amount + #{amount}, modify_time = now() " +
            "where user_id = #{userId} and account_type = #{accountType} and available_amount >= #{amount}")
    int decrAmount(AccountDTO accountDTO);


    /**
     * 扣减账户余额 TCC的confirm阶段
     * 冻结数量减少 总数量减少 可用数量不变
     */
    @Update("update t_account set frozen_amount = frozen_amount - #{amount}, total_amount = total_amount - #{amount}," +
            "modify_time = now() where user_id = #{userId} and account_type = #{accountType} and " +
            "frozen_amount >= #{amount} and total_amount >= #{amount}")
    int confirmDecrAmount(AccountDTO accountDTO);

    /**
     * 扣减账户余额 TCC的cancel阶段
     * 可用数量增加 冻结数量减少 总数量不变
     */
    @Update("update t_account set available_amount = available_amount + #{amount}," +
            "frozen_amount = frozen_amount - #{amount}, modify_time = now() " +
            "where user_id = #{userId} and account_type = #{accountType} and frozen_amount >= #{amount}")
    int cancelDecrAmount(AccountDTO accountDTO);


    /**
     * 增加账户余额 TCC的try阶段
     * 总数量增加 可用数量不变  预增加数量增加
     */
    @Update("update t_account set total_amount = total_amount + #{amount}, " +
            "pre_incr_amount = pre_incr_amount + #{amount}, modify_time = now() " +
            "where user_id = #{userId} and account_type = #{accountType}")
    int incrAmount(AccountDTO accountDTO);

    /**
     * 增加账户余额 TCC的confirm阶段
     * 总数量不变 可用数量增加  预增加数量减少
     */
    @Update("update t_account set available_amount = available_amount + #{amount}, " +
            "pre_incr_amount = pre_incr_amount - #{amount}, modify_time = now() " +
            "where user_id = #{userId} and account_type = #{accountType} and pre_incr_amount >= #{amount}")
    int confirmIncrAmount(AccountDTO accountDTO);

    /**
     * 增加账户余额 TCC的cancel阶段
     * 总数量减少 可用数量不变 预增加数量减少
     */
    @Update("update t_account set total_amount = total_amount 1 #{amount}, " +
            "pre_incr_amount = pre_incr_amount - #{amount}, modify_time = now() " +
            "where user_id = #{userId} and account_type = #{accountType} and pre_incr_amount >= #{amount} and total_amount >= #{amount}")
    int cancelIncrAmount(AccountDTO accountDTO);

    /**
     * 根据userId获取用户账户信息
     */
    @Select("select id, user_id, total_amount, available_amount, frozen_amount, account_type, create_time, modify_time " +
            "from t_account where user_id = #{userId} and account_type = #{accountType} limit 1")
    Account findByUserIdAndAccountType(Long userId, Integer accountType);
}
