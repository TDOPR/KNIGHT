//package com.defi.monitor;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//import org.web3j.abi.EventEncoder;
//import org.web3j.abi.EventValues;
//import org.web3j.abi.TypeReference;
//import org.web3j.abi.datatypes.Address;
//import org.web3j.abi.datatypes.Event;
//import org.web3j.abi.datatypes.generated.Uint256;
//import org.web3j.protocol.core.JsonRpc2_0Web3j;
//import org.web3j.protocol.core.Request;
//import org.web3j.protocol.core.Response;
//import org.web3j.protocol.core.methods.request.EthFilter;
//import org.web3j.protocol.core.methods.response.EthLog;
//
//import java.io.IOException;
//import java.util.Arrays;
//
///**
// * 服务监听器，继承ApplicationRunner，在spring启动时启动
// * @author liqiang
// */
//@Component
//public class ServiceRunner implements ApplicationRunner {
//    /**
//     * 日志记录
//     */
//    private Logger log = LoggerFactory.getLogger(ServiceRunner.class);
//
//
//    @Autowired
//    private JsonRpc2_0Web3j web3j;
//
//    //如果多个监听，必须要注入新的过滤器
//    @Autowired
//    private EthFilter uploadProAuth;
//
//    @Override
//    public void run(ApplicationArguments var1) throws Exception{
//        scanDataFilter();
//        this.log.info("This will be execute when the project was started!");
//    }
//
//
//    /**
//     * 收到上链事件
//     */
//    public void scanDataFilter(){
//        Event event = new Event("Transfer",
//                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {},
//                        (new TypeReference<Uint256>() {})));
//
//        uploadProAuth.addSingleTopic(EventEncoder.encode(event));
//        log.info("启动监听scanDataFilter");
//        Request request = web3j.ethGetLogs(uploadProAuth);
//        Response response = null;
//        try {
//            response = request.send();
//        } catch (IOException e) {
//            log.error(log.getName() + ".scanDataFilter.error.",  e);
//            throw new RuntimeException(e.getMessage());
//        }
//        System.out.println(response.getResult());
////        EthLog ethLog = (EthLog) response.getResult();
////            this.log.info("收到事件scanDataFilter");
////            EventValues eventValues = staticExtractEventParameters(event, log);
////            typedResponse._fromaddr = (String) eventValues.getIndexedValues().get(0).getValue();
////            typedResponse._product_id = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
////            typedResponse._rand_number =  (BigInteger)eventValues.getNonIndexedValues().get(1).getValue();
//
//    }
//}
