package com.defi.service;

import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

/**
 * 以太坊合约处理服务接口
 */
public interface ERC20WalletHandleService {

    /**
     * 查询订单属于哪个区块
     */
    BigInteger queryBlockByTransactionId(String transactionId);

    /**
     * 查询最新区块高度
     *
     * @return
     */
    BigInteger queryBlockLast();

    /**
     * 获取区块的交易信息
     *
     * @param blockNo
     * @return
     */
    List<EthBlock.TransactionResult> getTransactionsByBlockNo(BigInteger blockNo);

    /**
     * 获取区块的交易信息
     *
     * @param txHash
     * @return
     */
    TransactionReceipt getTransactionsByTxHash(String txHash);

    /**
     * 创建filter对象
     *
     * @return
     */
    EthFilter createFilter(BigInteger startBlockNumber, BigInteger endBlockNumber, String contractAddress);

    /**
     * 创建event对象
     *
     * @return
     */
    String createWithdrawEvent();
    String createRechargeEvent();
    String createRobotEvent();
    String createUserEvent();

    /**
     * 获取filter日志log
     *
     * @return
     */
    List<Log> getLogByFilter(EthFilter filter);

    /**
     * 根据区块hash获取区块信息
     *
     * @return
     */
    EthBlock.Block getBlockByHash(String blockHash);
}
