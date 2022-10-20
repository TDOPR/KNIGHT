package com.defi.maneger;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.defi.entity.CoinConfigEntity;
import com.defi.entity.EvmEventEntity;
import com.defi.mapper.CoinConfigDao;
import com.defi.service.CoinConfigService;
import com.defi.service.ERC20WalletHandleService;
import com.defi.service.EvmEventService;
import com.defi.service.impl.ERC20WalletHandleServiceImpl;
import com.defi.utils.Help;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("eventManager")
public class EventManager {

    @Autowired
    private CoinConfigDao coinConfigDao;

    @Autowired
    private ERC20WalletHandleService erc20WalletHandleService;

    @Autowired
    private CoinConfigService coinConfigService;

    @Autowired
    private TradeManager tradeManager;

    @Autowired
    private EvmEventService evmEventService;

    public void analyzeEvent() throws Exception {
        CoinConfigEntity scanDataConfig = coinConfigDao.getScanDataConfig();
        //获取最新区块高度
        BigInteger currentBlock = erc20WalletHandleService.queryBlockLast();
        //上次已检索的区块高度
        BigInteger oldBlock = scanDataConfig.getBlockNo();
        oldBlock = oldBlock.add(BigInteger.ONE);
        if (currentBlock.subtract(oldBlock).compareTo(new BigInteger("5000")) > -1) {
            currentBlock = oldBlock.add(new BigInteger("4999"));
        }
        log.info(log.getName() + ".scanDataJob currentBlock:{}, oldBlock:{}", currentBlock, oldBlock);
        execueteOneBlock(oldBlock, currentBlock);
        coinConfigDao.updateActionSeqById(scanDataConfig.getId(), currentBlock);
    }

    private void execueteOneBlock(BigInteger oldBlock, BigInteger toBlock) throws Exception {
        //获取需要扫描的事务合约地址
        QueryWrapper<CoinConfigEntity> coinConfigEntityQueryWrapper = new QueryWrapper<>();
        coinConfigEntityQueryWrapper.eq("coin", "EVENT");
        CoinConfigEntity eventCofing = coinConfigService.getOne(coinConfigEntityQueryWrapper);
        String eventContract = eventCofing.getContract();

        String rechargeEvent = erc20WalletHandleService.createRechargeEvent();
        String withdrawEvent = erc20WalletHandleService.createWithdrawEvent();
        String robotEvent = erc20WalletHandleService.createRobotEvent();
        String createUserEvent = erc20WalletHandleService.createUserEvent();

        EthFilter ethFilter = erc20WalletHandleService.createFilter(oldBlock, toBlock, eventContract);
        ethFilter.addOptionalTopics(rechargeEvent, withdrawEvent, robotEvent, createUserEvent);
        List<Log> logsList = erc20WalletHandleService.getLogByFilter(ethFilter);
        if (Help.isNull(logsList)) {
            return;
        }
        List<EvmEventEntity> evmEventEntityArrayList = new ArrayList<>();
        for (Log logsLog : logsList) {
            //判断事务是否已经处理
            String txHash = logsLog.getTransactionHash();
            if (evmEventService.getEventExistByTxHash(txHash)) {
                continue;
            }
            ArrayList topics = (ArrayList) logsLog.getTopics();
            String eventName = topics.get(0).toString();

            String address = "0x" + topics.get(1).toString().substring(2).replaceAll("^(0+)", "");
            String inviteCode;
            BigInteger amount;
            BigDecimal trueAmount;
            String decodeEventName = "";
            int type = 0;
            if (eventName.equalsIgnoreCase(rechargeEvent)) {
                //是充值
                amount = new BigInteger(topics.get(2).toString().substring(2), 16);
                trueAmount = new BigDecimal(amount).divide(BigDecimal.TEN.pow(18), 4, BigDecimal.ROUND_DOWN);
                tradeManager.tradeRecharge(address, trueAmount);
                decodeEventName = "投入";
            } else if (eventName.equalsIgnoreCase(withdrawEvent)) {
                //是提现
                amount = new BigInteger(topics.get(2).toString().substring(2), 16);
                trueAmount = new BigDecimal(amount).divide(BigDecimal.TEN.pow(18), 4, BigDecimal.ROUND_DOWN);
                type = new Integer(topics.get(3).toString().substring(2).replaceAll("^(0+)", ""));
                tradeManager.tradeWithdraw(address, trueAmount, type);
                decodeEventName = "提现";
            } else if (eventName.equalsIgnoreCase(robotEvent)) {
                //是买机器人
                amount = new BigInteger(topics.get(2).toString().substring(2), 16);
                trueAmount = new BigDecimal(amount).divide(BigDecimal.TEN.pow(18), 4, BigDecimal.ROUND_DOWN);
                tradeManager.tradeRobot(address, trueAmount);
                decodeEventName = "购买机器人";
            } else if (eventName.equalsIgnoreCase(createUserEvent)) {
                //注册
                inviteCode = topics.get(2).toString().substring(2).replaceAll("^(0+)", "");
                String inviteTenCode = new BigInteger(inviteCode, 16).toString();
                tradeManager.createUser(address, inviteTenCode);
                decodeEventName = "注册";
            }

            String blockHash = logsLog.getBlockHash();
            EthBlock.Block ethBlock = erc20WalletHandleService.getBlockByHash(blockHash);
            Date date = new Date(ethBlock.getTimestamp().intValue() * 1000L);

            EvmEventEntity ethScanDataEntity = EvmEventEntity.builder()
                    .txHash(txHash)
                    .blockNum(new BigInteger(logsLog.getBlockNumber().toString(10)))
                    .eventName(decodeEventName)
                    .createTime(date).build();
            evmEventEntityArrayList.add(ethScanDataEntity);
        }
        if (Help.isNotNull(evmEventEntityArrayList)) {
            evmEventService.insertIgnoreBatch(evmEventEntityArrayList);
        }
    }
}
