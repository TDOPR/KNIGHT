package com.defi.config;

import com.defi.service.impl.HttpSerivceEx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.JsonRpc2_0Web3j;

@Configuration
@Slf4j
public class ERC20Config {

    @Value(value = "${eth.point.url}")
    String ETH_POINT_URL;

//    @Value(value = "${eth.swap_token}")
//    String SWAP_TOKEN;

//    @Resource
//    private CoinConfigDao coinConfigDao;

    @Bean
    public Web3jService web3jService() {
        return new HttpSerivceEx(ETH_POINT_URL);
    }

    @Bean
    public Admin admin() {
        return Admin.build(web3jService());
    }

    @Bean
    public JsonRpc2_0Web3j jsonRpc() {
        return new JsonRpc2_0Web3j(web3jService());
    }

//    @Bean
//    @Scope("prototype")
//    @Autowired
//    public EthFilter ethFilter(JsonRpc2_0Web3j jsonRpc) throws IOException {
//        //获取启动时监听的区块
//        CoinConfigEntity scanDataConfig = coinConfigDao.getScanDataConfig();
//        BigInteger oldBlock = scanDataConfig.getBlockNo(); //上次已检索的区块高度
//        BigInteger fromblock = oldBlock.add(BigInteger.ONE);
//        return new EthFilter(DefaultBlockParameter.valueOf(fromblock),
//                DefaultBlockParameterName.LATEST, SWAP_TOKEN);
//    }
}
