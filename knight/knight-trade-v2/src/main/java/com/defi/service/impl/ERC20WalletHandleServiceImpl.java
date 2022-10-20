package com.defi.service.impl;

import com.defi.service.ERC20WalletHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.*;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * 以太坊合约处理类实现
 */
@Slf4j
@Component
public class ERC20WalletHandleServiceImpl implements ERC20WalletHandleService {

    @Autowired
    private JsonRpc2_0Web3j jsonRpc;

    /**
     * 查询区块高度
     *
     * @param transactionId
     */
    @Override
    public BigInteger queryBlockByTransactionId(String transactionId) {
        try {
            EthGetTransactionReceipt ethGetTransactionReceipt = jsonRpc.ethGetTransactionReceipt(transactionId).send();
            TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getResult();
            if (transactionReceipt == null) {
                return BigInteger.ZERO;
            }
            return new BigInteger(transactionReceipt.getBlockNumber().toString());
        } catch (Exception e) {
            log.error(log.getName() + ".queryBlockByTransactionId.error.transactionId=", transactionId, e);
            return BigInteger.ZERO;
        }
    }

    /**
     * 查询最新区块高度
     *
     * @return
     */
    @Override
    public BigInteger queryBlockLast() {

        try {
            EthBlockNumber ethBlockNumber = jsonRpc.ethBlockNumber().send();
            return ethBlockNumber.getBlockNumber();
        } catch (IOException e) {
            log.error(log.getName() + ".queryBlockLast.error", e);
            return BigInteger.ZERO;
        }
    }

    /**
     * 获取区块的信息
     *
     * @param blockNo
     * @return
     */
    @Override
    public List<EthBlock.TransactionResult> getTransactionsByBlockNo(BigInteger blockNo) {
        Request request = jsonRpc.ethGetBlockByNumber(new DefaultBlockParameterNumber(blockNo), true);
        Response response = null;
        try {
            response = request.send();
        } catch (IOException e) {
            log.error(log.getName() + ".getTransactionsByBlockNo.error.blockNo=", blockNo, e);
            throw new RuntimeException(e.getMessage());
        }
        EthBlock.Block block = (EthBlock.Block) response.getResult();
        return block.getTransactions();
    }

    /**
     * 获取区块logs的信息
     *
     * @param txHash
     * @return
     */
    @Override
    public TransactionReceipt getTransactionsByTxHash(String txHash) {
        Request request = jsonRpc.ethGetTransactionReceipt(txHash);
        Response response = null;
        try {
            response = request.send();
        } catch (IOException e) {
            log.error(log.getName() + ".getTransactionsByTxHash.error.txHash=", txHash, e);
            throw new RuntimeException(e.getMessage());
        }
        TransactionReceipt transactionReceipt = (TransactionReceipt) response.getResult();
        return transactionReceipt;
    }

    /**
     * 创建filter对象
     *
     * @return
     */
    @Override
    public EthFilter createFilter(BigInteger startBlockNumber, BigInteger endBlockNumber, String contractAddress) {
        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(startBlockNumber),
                DefaultBlockParameter.valueOf(endBlockNumber),
                contractAddress);
        return filter;
    }

    /**
     * 创建Withdraw的event对象
     *
     * @return
     */
    @Override
    public String createWithdrawEvent() {
        Event event = new Event("Withdraw",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {},
                        (new TypeReference<Uint256>() {}),
                        (new TypeReference<Uint256>() {})));
        return EventEncoder.encode(event);
    }

    /**
     * 创建robot的event对象
     *
     * @return
     */
    @Override
    public String createUserEvent() {
        Event event = new Event("Create",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {},
                        (new TypeReference<Uint256>() {})));
        return EventEncoder.encode(event);
    }

    /**
     * 创建robot的event对象
     *
     * @return
     */
    @Override
    public String createRobotEvent() {
        Event event = new Event("Robot",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {},
                        (new TypeReference<Uint256>() {})));
        return EventEncoder.encode(event);
    }

    /**
    * 创建Recharge的event对象
    *
    * @return
    */
    @Override
    public String createRechargeEvent() {
        Event event = new Event("Recharge",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {},
                        (new TypeReference<Uint256>() {})));
        return EventEncoder.encode(event);
    }

    /**
     * 获取filter日志log
     *
     * @return
     */
    @Override
    public List<Log> getLogByFilter(EthFilter filter) {
        Request request = jsonRpc.ethGetLogs(filter);
        Response response = null;
        try {
            response = request.send();
        } catch (IOException e) {
            log.error(log.getName() + ".getLogByFilter.error.txHash=", filter, e);
            throw new RuntimeException(e.getMessage());
        }
        List<Log> ethLog = (List) response.getResult();
        return ethLog;
    }

    /**
     * 根据区块hash获取区块信息
     *
     * @return
     */
    @Override
    public EthBlock.Block getBlockByHash(String blockHash) {
        Request request = jsonRpc.ethGetBlockByHash(blockHash,true);
        Response response = null;
        try {
            response = request.send();
        } catch (IOException e) {
            log.error(log.getName() + ".getBlockByHash.error.blockHash=", blockHash, e);
            throw new RuntimeException(e.getMessage());
        }
        EthBlock.Block ethBlock = (EthBlock.Block) response.getResult();
        return ethBlock;
    }
}
