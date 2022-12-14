package com.defi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.defi.entity.AccountDetailEntity;
import com.defi.entity.AccountEntity;
import com.defi.entity.UserInfoEntity;
import com.defi.enums.BusinessTypeEnum;
import com.defi.exception.AccountException;
import com.defi.mapper.AccountDao;
import com.defi.mapper.AccountDetailDao;
import com.defi.service.AccountService;
import com.defi.utils.Help;
import com.defi.utils.PageUtils;
import com.defi.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Service("accountService")
public class AccountServiceImpl extends ServiceImpl<AccountDao, AccountEntity> implements AccountService {

    @Autowired
    private AccountDetailDao accountDetailDao;

    @Autowired
    private AccountDao accountDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AccountEntity> page = this.page(
                new Query<AccountEntity>().getPage(params),
                new QueryWrapper<AccountEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<Map> selectStaticIncomeUserList() {
        return accountDao.selectStaticIncomeUserList();
    }

    @Override
    public int addPendingAmountByAid(@Param("accountId") long accountId, @Param("amount") BigDecimal amount, @Param("coinId") long coinId){
        return accountDao.addPendingAmountByAid(accountId, amount, coinId);
    }

    @Override
    public void addAccount(UserInfoEntity userEntity) {

        Date date = new Date();

        // ????????????????????????
        List<AccountEntity> list = new ArrayList<>();

        AccountEntity aht = new AccountEntity();
        aht.setUid(userEntity.getId());
        aht.setBalanceAmount(BigDecimal.ZERO);
        aht.setCoinId(1);
        aht.setSAmount(BigDecimal.ZERO);
        aht.setCreated(date);
        list.add(aht);

        AccountEntity lpAht = new AccountEntity();
        lpAht.setUid(userEntity.getId());
        lpAht.setBalanceAmount(BigDecimal.ZERO);
        lpAht.setCoinId(2);
        lpAht.setCreated(date);
        list.add(lpAht);

        AccountEntity ahtLp = new AccountEntity();
        ahtLp.setUid(userEntity.getId());
        ahtLp.setBalanceAmount(BigDecimal.ZERO);
        ahtLp.setCoinId(3);
        ahtLp.setCreated(date);
        list.add(ahtLp);

        AccountEntity ahtRLp = new AccountEntity();
        ahtRLp.setUid(userEntity.getId());
        ahtRLp.setBalanceAmount(BigDecimal.ZERO);
        ahtRLp.setCoinId(4);
        ahtRLp.setCreated(date);
        list.add(ahtRLp);

        AccountEntity ahtSLp = new AccountEntity();
        ahtSLp.setUid(userEntity.getId());
        ahtSLp.setBalanceAmount(BigDecimal.ZERO);
        ahtSLp.setCoinId(5);
        ahtSLp.setCreated(date);
        list.add(ahtSLp);

        AccountEntity qr = new AccountEntity();
        qr.setUid(userEntity.getId());
        qr.setBalanceAmount(BigDecimal.ZERO);
        qr.setCoinId(6);
        qr.setCreated(date);
        list.add(qr);

        this.saveBatch(list);

    }

    @Override
    public int addAmount(long userId, long coinId, BigDecimal amount) {
        AccountEntity accountEntity = this.queryByUserIdAndCoinId(userId, coinId);
        if (Help.isNull(accountEntity)) {
            throw new AccountException("??????????????????");
        } else {
            return baseMapper.addAmountByAid(accountEntity.getId(), amount, coinId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addAmount(long userId,
                             long coinId,
                             BigDecimal amount,
                             BusinessTypeEnum businessTypeEnum,
                             long orderId) throws AccountException {
        AccountEntity accountEntity = this.queryByUserIdAndCoinId(userId, coinId);
        if (Help.isNull(accountEntity)) {
            log.error("?????????????????????userId:{}, coinId:{}", userId, coinId);
            throw new AccountException("??????????????????");
        }
        if (baseMapper.addAmountByAid(accountEntity.getUid(), amount, coinId) > 0) {
            //????????????
            AccountDetailEntity accountDetailEntity = new AccountDetailEntity();
            accountDetailEntity.setAccountId(Integer.parseInt(accountEntity.getId() + ""));
            accountDetailEntity.setBusinessType(businessTypeEnum.getCode());
            accountDetailEntity.setUid(Integer.parseInt(userId + ""));
            accountDetailEntity.setCreated(new Date());
            accountDetailEntity.setCoinId(Integer.parseInt(coinId + ""));
            accountDetailEntity.setRemark(businessTypeEnum.getDesc());
            accountDetailEntity.setOrderId(Integer.parseInt(orderId + ""));
            accountDetailEntity.setDirection(1);
            accountDetailEntity.setRefAccountId(Integer.parseInt(accountEntity.getId() + ""));
            accountDetailEntity.setAmount(amount);
            accountDetailDao.insert(accountDetailEntity);

            return true;

        }
        log.error("?????????????????????orderId:{}, userId:{}, coinId:{}, amount:{}, businessTypeEnum:{}",
                orderId, userId, coinId, amount, businessTypeEnum.getCode());
        throw new AccountException("??????????????????");
    }

    /**
     * ????????????ID?????????????????????????????????
     *
     * @param userId ??????ID
     * @param coinId ??????ID
     * @return
     */
    @Override
    public AccountEntity queryByUserIdAndCoinId(long userId, long coinId) {
        QueryWrapper<AccountEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", userId)
                .eq("coin_id", coinId)
//                .eq("status", 1)
                .last("LIMIT 1");
        List<AccountEntity> accountList = baseMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(accountList)) {
            return null;
        }
        return accountList.get(0);
    }

    @Override
    public int subtractAmount(long userId, long coinId, BigDecimal amount) {
        return baseMapper.subtractAmount(userId, coinId, amount);
    }

    @Override
    public boolean subtractAmount(long userId, long coinId, BigDecimal amount, BusinessTypeEnum businessTypeEnum, long orderId) throws AccountException {
        AccountEntity accountEntity = this.queryByUserIdAndCoinId(userId, coinId);
        if (Help.isNull(accountEntity)) {
            log.error("????????????-?????????????????????userId:{}, coinId:{}", userId, coinId);
            throw new AccountException("??????????????????");
        }

        if (baseMapper.subtractAmount(userId, coinId, amount) > 0) {
            //????????????
            AccountDetailEntity accountDetailEntity = new AccountDetailEntity();
            accountDetailEntity.setCreated(new Date());
            accountDetailEntity.setCoinId(Integer.parseInt(coinId + ""));
            accountDetailEntity.setRemark(businessTypeEnum.getDesc());
            accountDetailEntity.setBusinessType(businessTypeEnum.getCode());
            accountDetailEntity.setUid(Integer.parseInt(userId + ""));
            accountDetailEntity.setOrderId(Integer.parseInt(orderId + ""));
            accountDetailEntity.setDirection(2);
            accountDetailEntity.setAccountId(Integer.parseInt(accountEntity.getId() + ""));
            accountDetailEntity.setRefAccountId(Integer.parseInt(accountEntity.getId() + ""));
            accountDetailEntity.setAmount(amount);
            accountDetailDao.insert(accountDetailEntity);

            return true;
        }
        log.error("?????????????????????orderId:{}, userId:{}, coinId:{}, amount:{}, businessTypeEnum:{}",
                orderId, userId, coinId, amount, businessTypeEnum.getCode());
        throw new AccountException("??????????????????");
    }

    @Override
    public int addAmountByAid(long accountId, BigDecimal amount, long coinId) {

        return accountDao.addAmountByAid(accountId, amount, coinId);

    };

    @Override
    public List<Map> selectSuperUserList(){
        return accountDao.selectSuperUserList();
    }

    @Override
    public int addSuperAmountByAid(long accountId, BigDecimal amount, long coinId){
        return accountDao.addSuperAmountByAid(accountId, amount, coinId);
    }

    @Override
    public int selectAllAmount(){
        return accountDao.selectAllAmount();
    }
}
