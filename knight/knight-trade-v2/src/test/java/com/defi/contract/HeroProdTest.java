//package com.defi.contract;
//
//import com.defi.entity.CoinConfigEntity;
//import com.defi.mapper.CoinConfigDao;
//import com.defi.service.*;
//import com.defi.utils.DESUtil;
//import com.defi.utils.Help;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.web3j.abi.FunctionEncoder;
//import org.web3j.abi.TypeReference;
//import org.web3j.abi.datatypes.Address;
//import org.web3j.abi.datatypes.Type;
//import org.web3j.abi.datatypes.generated.Uint256;
//import org.web3j.abi.datatypes.generated.Uint8;
//import org.web3j.crypto.*;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.core.DefaultBlockParameterName;
//import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
//import org.web3j.protocol.core.methods.response.EthSendTransaction;
//import org.web3j.protocol.http.HttpService;
//import org.web3j.utils.Numeric;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.math.BigInteger;
//import java.util.*;
//import java.util.stream.Collectors;
//
//
//@RunWith(SpringRunner.class)
//@Slf4j
//@SpringBootTest
//public class HeroProdTest {
//
//    @Autowired
//    private UserRechargeService userRechargeService;
//
//    @Autowired
//    CoinAddressPoolService coinAddressPoolService;
//
//    @Resource
//    private CoinConfigDao coinConfigDao;
//    @Autowired
//    private ETHTokenConfirmJobService confirmJobService;
//
//    @Autowired
//    private DESUtil desUtil;
//
//    @Autowired
//    private CredentialsService credentialsService;
//
//    @Test
//    public void rechargeJob() {
//        userRechargeService.rechargeJob();
//    }
//
//    @Test
//    public void export() throws CipherException, IOException {
//        String pri = credentialsService.getSenderCredentials("0x4c0d0bdae6a344cb31cdd00955d0267cca180a25").getEcKeyPair().getPrivateKey().toString(16);
//        System.out.println(pri);
//    }
//
//    @Test
//    public void toStringS() {
//        //Original byte[]
////        byte[] bytes = "0000000000000000000000000000000000000000000000000000000000000140".getBytes();
//        byte[] bytes = new byte[2];
////        System.out.println(bytes[0]);
////        System.out.println(bytes[1]);
//        bytes[0] = 0000000000000000000000000000000000000000000000000000000000000140;
//        bytes[1] = 40;
//        //Base64 Encoded
//        String encoded = Base64.getEncoder().encodeToString(bytes);
//
//        //Base64 Decoded
//        byte[] decoded = Base64.getDecoder().decode(encoded);
//
//        //Verify original content
//        System.out.println(encoded);
//        System.out.println(new String(decoded));
//
//        byte[] params3 = new byte[2];
////        params3[0]= (byte) Integer.parseInt("fc", 16);
//        System.out.println(params3[0]);
//        System.out.println();
//        params3[0] = Integer.valueOf("fc", 16).byteValue();
//        System.out.println(params3[0]);
//
//        params3[1] = (byte) 252;
//        System.out.println(params3[1]);
//
//    }
//
//    @Test
//    public void testMethod() throws Exception {
//        Web3j testWeb3 = Web3j.build(new HttpService("https://http-testnet.hecochain.com"));
//        Credentials credentials = Credentials.create("b556b3095e450feb9bdfa7728a77b6193102d37da8caa649a7e90b5551ae6db9");
//        String fromAddress = "0x47CB2cf4b8B4B3c57B277c85cf0cB455c09dC705";
//        // 车nft合约
//        String contractAddress = "0xDb40Dbf8B3670398BBa72CD51f90B3f653f8f40f";
//
//        EthGetTransactionCount nonceCount = testWeb3.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).send();
//        BigInteger transactionCount = nonceCount.getTransactionCount();
//
//
//        //发行赛车:mint(address _to, uint256 _tokenId, uint256 _value, uint8 _level)   //需要管理员权限
//        String _to = "0x706d1d4b2236a30caa3d6df61cfc4b59fa309d01";
//        BigInteger _tokenId = new BigInteger("14");
//        BigInteger _value = new BigInteger("120");
//        BigInteger _level = BigInteger.ONE;
//        String nonceValue = transactionCount + "";
//
//        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
//                "mint",
//                Arrays.<Type>asList(new Address(_to),
//                        new Uint256(_tokenId),
//                        new Uint256(_value),
//                        new Uint8(_level)),
//                Collections.<TypeReference<?>>emptyList());
//
//        BigInteger gasLimit = new BigInteger("1000000");
//        log.info("gasLimit：" + gasLimit);
//
//        BigInteger gasPrice = new BigInteger("1000000000");
//        log.info("gasPrice：" + gasPrice);
//
//        String data = FunctionEncoder.encode(function);
//
//        RawTransaction transaction = RawTransaction.createTransaction(
//                new BigInteger(nonceValue),
//                gasPrice,
//                gasLimit,
//                contractAddress,
//                data);
//
//        String hexValue = Numeric.toHexString(TransactionEncoder.signMessage(transaction, 256, credentials));
//        EthSendTransaction trx = testWeb3.ethSendRawTransaction(hexValue).send();
//        String transactionHash = trx.getTransactionHash();
//        System.out.println(transactionHash);
//    }
//
//    @Test
//    public void excueteOneBlock() {
//        List<CoinConfigEntity> list = coinConfigDao.getEnableAll();
//
//        Map<String, CoinConfigEntity> cmap = list.stream().collect(Collectors.toMap(CoinConfigEntity::getCoin, a -> a, (k1, k2) -> k1));
//        CoinConfigEntity ethCoinConfig = cmap.get("BNB");
//
//        Map<String, CoinConfigEntity> tokenConfigMap = list.stream().filter(configEntity -> Help.isNotNull(configEntity.getContract())).collect(Collectors.toMap(CoinConfigEntity::getContract, a -> a, (k1, k2) -> k1));
//
//        userRechargeService.excueteOneBlock(ethCoinConfig, tokenConfigMap, new BigInteger("9908786"));
//    }
//
//    @Test
//    public void batchCreateEthAddress() {
//        coinAddressPoolService.batchCreateEthAddress();
//    }
//
//    @Test
//    public void addressXPri() {
//        String pwd = "72D47088F9758F583D99F41199539321E9F488CF065CE8F0790E9DF30FE7546BA56A07CB565743BB";
//        String keystore = "88FE8429606D0601B28142E195B9AEE476E6D34E609D11F147F830A4FC3182AF18FA167B2143D572C299277C1FFE9B240B333F7B06176752D2DD85F9F5FAB2CF45A66FAC0062C691C8025F37249BF172113C4890AC42F18D62AAEE3BD30DD4397B0F9BD04F5C8131DBEB2FCD525947DB2F5A1968AD2D9572DB05C82E98D8ED2987F536DB82C541E57856C189465CFA561E1B757F99E4E5F59F50E3928118015CC445BF696A344759E789890624C3F059F11FB038D56E8D8BA22DC0F3456FA696AA2A5A5F9DBE7125E66A9C84A9AD2BDA8C97AED1394FF54432D380D909E68CC2D33EFA3D29CE9B4F40A3DDA21F1CDC34972987FEE5AA396EBC9EDBDC25C915D34868AAEA269D80A1DB3F9FF9823CB1725E6B01AE2E99AF537E33181132D51FCECCE37CAE46B4D0501975CE2389413F0C9D727F8469A717B77424B431DB1FBC3A65FC08E95F56487F3B4EA62B5F2BEDE5EE6C6B780EF6A497F5418FC6BC36D2AFBBE0B38861835C3EE4851ECDD6865A5D0D80C3F17DB68FD189E03A859379E6F46F93B6785FAF506F5CAF08EAC149AC5B328CB2907FA3E0027E05B706AE42EA23D943F6EE5C12AC6CAED1D978670C807FF0B2394DB05E373A2319C29D54FE76717FED4E3A2F379704C527FA0C67388E144720079161D7756A8ABCE10A92701D589836C45B26122012942DCFE31AEDE79B";
//
//        String password = desUtil.decrypt(pwd);
//        String privateKey = desUtil.decrypt(keystore);
//
////        System.out.println(password);
////        System.out.println(privateKey);
//
//        String pri = "2322c99a3d927a51de22bacb3fa20604da8cb3873798edddd0bc82f3829eabd";
//        if (pri.length() == 63) {
//            pri = "0" + pri;
//        }
//        System.out.println(pri);
//    }
//
//    //智能合约传输byte类型使用
//    @Test
//    public void stringToBytes() {
//        String s = "https://ccrdata.oss-cn-beijing.aliyuncs.com/2.json";
//        String str = "";
//        for (int i = 0; i < s.length(); i++) {
//            int ch = s.charAt(i);
//            String s4 = Integer.toHexString(ch);
//            str = str + s4;
//        }
//        System.out.println("0x" + str);
//    }
//
//    @Test
//    public void string16ToString() {
////        String s = "5b833391c86f727e288051b19432127ffa23fa1ee5ae6b410946f91417a8194";
//        String s = "45977d03";
//        s = s.replace(" ", "");
//        byte[] baKeyword = new byte[s.length() / 2];
//        for (int i = 0; i < baKeyword.length; i++) {
//            try {
//                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            s = new String(baKeyword, "UTF-8");
//            new String();
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//        System.out.println(s);
//    }
//
//    @Test
//    public void methodId() {
//        String s = "b3a1fdee3fa244ebb3630dbfce8e8c2bceaf3fcf9776a32848b6724acfadb289";
//
//    }
//
//    @Test
//    public void queryBlockJob() {
//        confirmJobService.queryBlockJob();
//        confirmJobService.confirmJob();
//    }
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @Autowired
//    UserWithdrawService userWithdrawService;
//
//    @Test
//    public void orderJob() {
////        try {
////            boolean bool = redisTemplate.opsForValue().setIfAbsent(TaskRedisCheckKey.ETTC_ETH_TOKEN_WITHDRAW_JOB_REDIS_CHECK_KEY, "", Duration.ZERO.ofSeconds(60 * 5));
////            if (bool) {
////                userWithdrawService.order();
////                redisTemplate.delete(TaskRedisCheckKey.ETTC_ETH_TOKEN_WITHDRAW_JOB_REDIS_CHECK_KEY);
////            } else {
////                log.error(log.getName() + "提币任务失败，上一任务正在运行!");
////            }
////        } catch (Exception e) {
////            log.error("提币任务失败,失败信息【{}】", e);
////            redisTemplate.delete(TaskRedisCheckKey.ETTC_ETH_TOKEN_WITHDRAW_JOB_REDIS_CHECK_KEY);
////            e.printStackTrace();
////        }
//    }
//
//}
