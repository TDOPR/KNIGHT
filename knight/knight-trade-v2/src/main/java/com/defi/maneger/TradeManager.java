package com.defi.maneger;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.defi.entity.*;
import com.defi.enums.BusinessTypeEnum;
import com.defi.enums.CoinEnum;
import com.defi.enums.CommuRewardEnum;
import com.defi.enums.RobotEnum;
import com.defi.mapper.AccountDao;
import com.defi.mapper.DataPerdayDao;
import com.defi.service.*;
import com.defi.utils.Help;
import com.defi.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Int;

import javax.security.auth.login.AccountException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class TradeManager {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDetailService accountDetailService;

    @Autowired
    private UserOrderService userOrderService;

    @Autowired
    private TreePathsService treePathsService;

    @Autowired
    private RewardFreezeRobotService rewardFreezeRobotService;

    @Autowired
    private UserWithdrawService userWithdrawService;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private DataPerdayService dataPerdayService;

    @Autowired
    private DataPerdayDao dataPerdayDao;


    private final static BigDecimal FEE_RATE = new BigDecimal("0.01");
    private final static BigDecimal LI_FEE_RATE = new BigDecimal("0.02");

    private final static Integer STATIC_RATE_MIN = 5;
    private final static Integer STATIC_RATE_MAX = 13;

    public void staticIncome() {
        //获取当日的收益比例
        Random r = new Random();
        int rR = r.nextInt(100);
        int rRate = 0;

        if (rR >= 0 && rR <= 69) {
            rRate = 45;
        } else if (rR >= 70 && rR <= 79) {
            rRate = 50;
        } else if (rR >= 80 && rR <= 89) {
            rRate = 60;
        } else if (rR >= 90 && rR <= 93) {
            rRate = 70;
        } else if (rR >= 94 && rR <= 97) {
            rRate = 80;
        } else if (rR >= 98 && rR <= 99) {
            rRate = 90;
        }

        BigDecimal staticRate = new BigDecimal(rRate).divide(new BigDecimal(10000), 4, BigDecimal.ROUND_DOWN);

        BigDecimal staticRobotRate = new BigDecimal(rRate).divide(new BigDecimal(10000), 4, BigDecimal.ROUND_DOWN).multiply(BigDecimal.ONE.add(new BigDecimal(30).divide(new BigDecimal(100))));

        List<Map> superUserList = accountService.selectSuperUserList();
        for (Map accountMap : superUserList) {
            int uid = Integer.parseInt(accountMap.get("id").toString());
//            BigDecimal samount = new BigDecimal(accountMap.get("samount").toString());

//            QueryWrapper<DataPerdayEntity> dataPerdayEntityQueryWrapper = new QueryWrapper<>();
//            dataPerdayEntityQueryWrapper.eq("id", 1);
//            DataPerdayEntity dataPerdayEntity = dataPerdayService.getOne(dataPerdayEntityQueryWrapper);


            BigDecimal allAmount = new BigDecimal(accountService.selectAllAmount());
            BigDecimal value = allAmount.multiply(staticRate);

            accountService.addSuperAmountByAid(uid, value, CoinEnum.SUPER_STATIC_USDT.getCoinId());
        }

        List<Map> userList = accountService.selectStaticIncomeUserList();
        for (Map userMap : userList) {
            int uid = Integer.parseInt(userMap.get("uid").toString());
            BigDecimal amount = new BigDecimal(userMap.get("amount").toString());

            QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
            userInfoEntityQueryWrapper.eq("id", uid);
            UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);

            amount = amount.multiply(staticRate);

            if (userInfoEntity.getRobotLevel() != 0) {
                amount = amount.multiply(staticRobotRate);
            } else {
                amount = amount.multiply(staticRate);
            }


            if (userInfoEntity.getRechargeMax().compareTo(userInfoEntity.getRechargeMax().add(amount)) < 0) {
                accountService.addPendingAmountByAid(uid, amount, CoinEnum.STATIC_USDT.getCoinId());
            }

            accountService.addAmountByAid(uid, amount, CoinEnum.STATIC_USDT.getCoinId());
            commuTeamReward(uid, amount, CoinEnum.SUPER_STATIC_USDT.getCoinId());
            commuAlgebraPrize(uid, amount);
        }
    }

    public void tradeRecharge(String userAddress, BigDecimal amount) throws AccountException {
        if (!existUser(userAddress)) {
            return;
        }

        QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
        userInfoEntityQueryWrapper.eq("address", userAddress);
        UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);

        if (!upgradeRechargeAmount(userInfoEntity, amount, 1)) {
            return;
        }

//        dataPerdayService.addAmount(amount);
        //生成订单
        UserOrderEntity userOrderEntity = UserOrderEntity.builder()
                .address(userAddress)
                .type(0)
                .uid(userInfoEntity.getId())
                .amount(amount)
                .createTime(new Date()).build();
//        userOrderService.updateById(userOrderEntity);
        userOrderService.save(userOrderEntity);
        //生成账单
        accountService.addAmount(userInfoEntity.getId(), CoinEnum.RECHARGE_USDT.getCoinId(), amount, BusinessTypeEnum.RECHARGE, userOrderEntity.getId());
        updatePerformance(userInfoEntity.getId(), amount, true);
    }

    public void tradeWithdraw(String userAddress, BigDecimal amount, int type) throws AccountException {
        if (!existUser(userAddress)) {
            return;
        }

        QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
        userInfoEntityQueryWrapper.eq("address", userAddress);
        UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);

        Date date = new Date();


        if (!upgradeRechargeAmount(userInfoEntity, amount, 2)) {
            return;
        }

        dataPerdayService.substractAmount(amount);
        //生成订单
        UserOrderEntity userOrderEntity = UserOrderEntity.builder()
                .address(userAddress)
                .type(type)
                .uid(userInfoEntity.getId())
                .amount(amount)
                .createTime(new Date()).build();
        userOrderService.save(userOrderEntity);

        accountService.subtractAmount(userInfoEntity.getId(), CoinEnum.RECHARGE_USDT.getCoinId(), amount, BusinessTypeEnum.RECHARGE, userOrderEntity.getId());
        updatePerformance(userInfoEntity.getId(), amount, false);

        //收取手续费
        BigDecimal fee = amount.multiply(FEE_RATE);
        if (fee.compareTo(BigDecimal.ONE) < 0) {
            fee = BigDecimal.ONE;
        }

        BigDecimal Lfee = BigDecimal.ZERO;
        if (isLatestWeek(userInfoEntity.getCreated(), date)) {
            Lfee = amount.multiply(LI_FEE_RATE);
        }

        BigDecimal mum = amount.subtract(fee.add(Lfee));

        //生成提现记录
        UserWithdrawEntity userWithdrawEntity = UserWithdrawEntity.builder()
                .address(userAddress)
                .id(userInfoEntity.getId())
                .coinName("USDT")
                .coinType("EVM")
                .coinId(1)
                .num(amount)
                .fee(fee)
                .mum(mum)
                .status(4)
                .lastUpdateTime(new Date())
                .created(new Date()).build();
        userWithdrawService.save(userWithdrawEntity);
    }

    private boolean upgradeRechargeAmount(UserInfoEntity userInfoEntity, BigDecimal amount, int type) {
        BigDecimal rechargeMax = userInfoEntity.getRechargeMax();
        QueryWrapper<AccountEntity> accountEntityQueryWrapper = new QueryWrapper<>();
        accountEntityQueryWrapper.eq("uid", userInfoEntity.getId());
        accountEntityQueryWrapper.eq("coin_id", CoinEnum.RECHARGE_USDT.getCoinId());
        AccountEntity accountEntity = accountService.getOne(accountEntityQueryWrapper);
        BigDecimal alreadyRecharge = accountEntity.getBalanceAmount();
//        BigDecimal balanceAmount = accountEntity.getBalanceAmount();
        if (type == 1) {
            BigDecimal newAlreadyRecharge = alreadyRecharge.add(amount);
            if (rechargeMax.compareTo(newAlreadyRecharge) < 0) {
                return false;
            }
//            userInfoEntity.setRechargeMax(newAlreadyRecharge);
            accountEntity.setBalanceAmount(newAlreadyRecharge);
//            userInfoService.updateById(userInfoEntity);
            accountService.updateById(accountEntity);

            return true;
        } else {
            BigDecimal newAlreadyRecharge = alreadyRecharge.subtract(amount);
            accountEntity.setBalanceAmount(newAlreadyRecharge);
//            userInfoEntity.setRechargeMax(newAlreadyRecharge);
//            userInfoService.updateById(userInfoEntity);
            accountService.updateById(accountEntity);
            return true;
        }
    }

    public void tradeRobot(String userAddress, BigDecimal amount) throws Exception {
        if (!existUser(userAddress)) {
            return;
        }
        //判断当前用户是否已有机器人，查看是首次购买还是补差价
        QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
        userInfoEntityQueryWrapper.eq("address", userAddress);
        UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);
        int alreadyRobotLevel = userInfoEntity.getRobotLevel();

        int type;
        BusinessTypeEnum businessTypeEnum;
        int robotLevel = 0;
        if (alreadyRobotLevel == 0) {
            //首次购买机器人
            type = 1;
            businessTypeEnum = BusinessTypeEnum.BUY_ROBOT;
            if (amount.compareTo(RobotEnum.ROBOT_ONE.getPrice()) == 0) {
                robotLevel = 1;
            } else if (amount.compareTo(RobotEnum.ROBOT_TW0.getPrice()) == 0) {
                robotLevel = 2;
            } else if (amount.compareTo(RobotEnum.ROBOT_THREE.getPrice()) == 0) {
                robotLevel = 3;
            } else if (amount.compareTo(RobotEnum.ROBOT_FOUR.getPrice()) == 0) {
                robotLevel = 4;
            }
            //更新我的上级有效直推数量
            QueryWrapper<UserInfoEntity> inviteEntityWrapper = new QueryWrapper<>();
            inviteEntityWrapper.eq("id", userInfoEntity.getDirectInviteid());
            UserInfoEntity inviteEntity = userInfoService.getOne(inviteEntityWrapper);
            inviteEntity.setDirectNum(inviteEntity.getDirectNum() + 1);
            userInfoService.updateById(inviteEntity);
        } else {
            //补差价升级机器人
            type = 2;
            businessTypeEnum = BusinessTypeEnum.UPGRADE_ROBOT;
            BigDecimal alreadyRobotAmount = BigDecimal.ZERO;
            if (alreadyRobotLevel == RobotEnum.ROBOT_ONE.getCode()) {
                alreadyRobotAmount = RobotEnum.ROBOT_ONE.getPrice();
            } else if (alreadyRobotLevel == RobotEnum.ROBOT_TW0.getCode()) {
                alreadyRobotAmount = RobotEnum.ROBOT_TW0.getPrice();
            } else if (alreadyRobotLevel == RobotEnum.ROBOT_THREE.getCode()) {
                alreadyRobotAmount = RobotEnum.ROBOT_THREE.getPrice();
            } else if (alreadyRobotLevel == RobotEnum.ROBOT_FOUR.getCode()) {
                alreadyRobotAmount = RobotEnum.ROBOT_FOUR.getPrice();
            }
            BigDecimal currentRobotAmount = alreadyRobotAmount.add(amount);
            if (currentRobotAmount.compareTo(RobotEnum.ROBOT_TW0.getPrice()) == 0) {
                robotLevel = 2;
            } else if (currentRobotAmount.compareTo(RobotEnum.ROBOT_THREE.getPrice()) == 0) {
                robotLevel = 3;
            } else if (currentRobotAmount.compareTo(RobotEnum.ROBOT_FOUR.getPrice()) == 0) {
                robotLevel = 4;
            }
        }

        //生成订单
        UserOrderEntity userOrderEntity = UserOrderEntity.builder()
                .address(userAddress)
                .type(type)
                .uid(userInfoEntity.getId())
                .amount(amount)
                .createTime(new Date()).build();
        userOrderService.save(userOrderEntity);
        //生成账单
        accountDetailService.generateBill(userInfoEntity.getId(), businessTypeEnum, userOrderEntity.getId(), amount);
        //修改机器人等级，以及用户的托管上限
        UserInfoEntity updateUserInfoEntity = UserInfoEntity.builder()
                .id(userInfoEntity.getId())
                .rechargeMax(RobotEnum.getRechargeMaxByCode(robotLevel))
                .robotLevel(robotLevel).build();
        userInfoService.updateById(updateUserInfoEntity);
        //判断我是否有冻结的机器人推广收益需要发放
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String deadTime = sdf.format(date);
//        BigDecimal unFreezeReward = new BigDecimal(rewardFreezeRobotService.getRewardFreezeRobotAmount(userInfoEntity.getId(), robotLevel, deadTime).get("amount")
//                .toString());
        BigDecimal unFreezeReward = new BigDecimal(rewardFreezeRobotService.getRewardFreezeRobotAmount(userInfoEntity.getId(), robotLevel, deadTime)+"");
        if (unFreezeReward.compareTo(BigDecimal.ZERO) > 0) {
            accountService.addAmount(userInfoEntity.getId(), CoinEnum.ROBOT_USDT.getCoinId(), unFreezeReward, BusinessTypeEnum.REWARD_UNFREEZE_ROBOT, userOrderEntity.getId());
            accountService.subtractAmount(userInfoEntity.getId(), CoinEnum.ROBOT_FREEZE_USDT.getCoinId(), unFreezeReward, BusinessTypeEnum.REWARD_UNFREEZE_ROBOT, userOrderEntity.getId());
        }

        //更新业绩
        updatePerformance(userInfoEntity.getId(), amount, true);
        //发机器人推广奖
        robotReward(userInfoEntity.getDirectInviteid(), robotLevel, amount, userOrderEntity.getId());
    }


    private boolean existUser(String userAddress) {
        //判断当前用户是否存在
        QueryWrapper<UserInfoEntity> userWrapper = new QueryWrapper<>();
        userWrapper.eq("address", userAddress);
        UserInfoEntity createUserEntity = userInfoService.getOne(userWrapper);
        return Help.isNotNull(createUserEntity);
    }

    private boolean isLatestWeek(Date addtime, Date now) {
        Calendar calendar = Calendar.getInstance();  //得到日历
        calendar.setTime(addtime);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为7天前
        Date after7days = calendar.getTime();   //得到7天前的时间
        if (after7days.getTime() < now.getTime()) {
            return false;
        } else {
            return true;
        }
    }

    public void createUser(String userAddress, String inviteUserCode) throws Exception {
        //需要注册用户
        userInfoService.createUser(userAddress, inviteUserCode);
    }

    private void updatePerformance(int userId, BigDecimal amount, boolean type) {
        QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
        treePathsEntityQueryWrapper.eq("descendant", userId);
        List<TreePathsEntity> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);
        for (TreePathsEntity treePathsEntity : treePathsEntityList) {
            if (type) {
                userInfoService.addTeamPerformanceById(treePathsEntity.getAncestor(), amount);
            } else {
                QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
                userInfoEntityQueryWrapper.eq("id", treePathsEntity.getAncestor());
                UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);
                if (userInfoEntity.getTeamPerformance().compareTo(amount) < 0) {
                    amount = userInfoEntity.getTeamPerformance();
                }
                userInfoService.subtractTeamPerformanceById(treePathsEntity.getAncestor(), amount);
            }
            levelChange(treePathsEntity.getAncestor());
        }
    }

    private void levelChange(int userId) {
        QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
        treePathsEntityQueryWrapper.eq("ancestor", userId);
        treePathsEntityQueryWrapper.eq("level", 1);
        List<TreePathsEntity> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);
        QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
        userInfoEntityQueryWrapper.eq("id", userId);
        UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);

        int teamLevelOneNum = 0;
        int teamLevelTwoNum = 0;
        int teamLevelThreeNum = 0;
        int teamLevelFourNum = 0;
        int userLevel = userInfoEntity.getLevel();
        boolean levelChangeFlag = false;
        for (TreePathsEntity treePathsEntity : treePathsEntityList) {
            List<Map> levelList = treePathsService.getNumByLevel(treePathsEntity.getDescendant());
            for (Map levelMap : levelList) {
                int level = Integer.parseInt(levelMap.get("LEVEL").toString());
                int num = Integer.parseInt(levelMap.get("NUM").toString());
//                if (num > 0) {
//                    switch (level) {
//                        case 4:
////                            teamLevelFourNum++;
//                            teamLevelFourNum += num;
//                            break;
//                        case 3:
////                            teamLevelThreeNum++;
//                            teamLevelThreeNum += num;
//                            break;
//                        case 2:
////                            teamLevelTwoNum++;
//                            teamLevelTwoNum += num;
//                            break;
//                        case 1:
////                            teamLevelTwoNum++;
//                            teamLevelOneNum += num;
//                            break;
//                        default:
//                            break;
//                    }
//                }
                switch (level) {
                    case 4:
//                            teamLevelFourNum++;
                        teamLevelFourNum += num;
                        break;
                    case 3:
//                            teamLevelThreeNum++;
                        teamLevelThreeNum += num;
                        break;
                    case 2:
//                            teamLevelTwoNum++;
                        teamLevelTwoNum += num;
                        break;
                    case 1:
//                            teamLevelTwoNum++;
                        teamLevelOneNum += num;
                        break;
                    default:
                        break;

                }
            }
            if (teamLevelFourNum >= 3 && userLevel != 5) {
                userLevel = 5;
                levelChangeFlag = true;
            } else if (teamLevelThreeNum >= 3 && userLevel != 4) {
                userLevel = 4;
                levelChangeFlag = true;
            } else if (teamLevelTwoNum >= 3 && userLevel != 3) {
                userLevel = 3;
                levelChangeFlag = true;
            } else if (teamLevelOneNum >= 3 && userLevel != 2) {
                userLevel = 2;
                levelChangeFlag = true;
            } else {
                if (userInfoEntity.getDirectNum() >= 10
                        && userInfoEntity.getTeamPerformance().compareTo(new BigDecimal(30000)) > -1
                        && userLevel != 1) {
                    userLevel = 1;
                    levelChangeFlag = true;
                }
            }
            if (levelChangeFlag) {
                UserInfoEntity updateUserEntity = UserInfoEntity.builder()
                        .id(userId)
                        .level(userLevel).build();
                userInfoService.updateById(updateUserEntity);
            }

        }
    }

    private void commuTeamReward(int userId, BigDecimal amount, long coinId) {


        QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
        userInfoEntityQueryWrapper.eq("id", userId);
        UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);

//        String inviterId = userInfoEntity.getInviteCode();

        QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
        treePathsEntityQueryWrapper.eq("descendant", userId);
        treePathsEntityQueryWrapper.ne("ancestor", userId);
//        treePathsEntityQueryWrapper.eq("level", 1);
        List<TreePathsEntity> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);

//        int userLevel = userInfoEntity.getLevel();

        boolean teamRewardFlag = false;
        BigDecimal lastRate;
        int lastLevel = 0;
        BigDecimal rate = BigDecimal.ZERO;

        for (TreePathsEntity treePathsEntity : treePathsEntityList) {
//            List<Map> userList = treePathsService.getLevelById(treePathsEntity.getAncestor());
            Map userMap = treePathsService.getLevelById(treePathsEntity.getAncestor());
            int id = Integer.parseInt(userMap.get("ID").toString());
            int level = Integer.parseInt(userMap.get("LEVEL").toString());
            if (level > lastLevel){
                teamRewardFlag = true;
                rate = CommuRewardEnum.getRateByLevel(level);
            }
//            for (Map userMap : userList) {
//                int level = Integer.parseInt(userMap.get("level").toString());
//                int uid = Integer.parseInt(userMap.get("uid").toString());
//                if (level > lastLevel) {
//                    teamRewardFlag = true;
//                    rate = CommuRewardEnum.getRateByLevel(level);
//
//                }
            if (teamRewardFlag) {
                lastRate = rate;
                amount = amount.multiply(rate.subtract(lastRate)).divide(new BigDecimal(100), 4, BigDecimal.ROUND_DOWN);
//                lastRate = rate;
                lastLevel = level;
                AccountEntity updateUserAccountEntity = AccountEntity.builder()
                        .id(id)
                        .floatProfit(amount)
                        .coinId(Integer.parseInt((coinId) + "")).build();
                accountService.updateById(updateUserAccountEntity);
            }
        }
    }



    private void commuAlgebraPrize(int userId, BigDecimal amount) {

//        QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
//        userInfoEntityQueryWrapper.eq("id", userId);
//        UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);

//        String inviteId = userInfoEntity.getInviteCode();

        QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
        treePathsEntityQueryWrapper.eq("descendant", userId);
        treePathsEntityQueryWrapper.ne("ancestor", userId);
        List<TreePathsEntity> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);
        int i = 1;
        int directNum = 0;
        BigDecimal rate = new BigDecimal(50);

        for (TreePathsEntity treePathsEntity : treePathsEntityList) {

            QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper1 = new QueryWrapper<>();
            userInfoEntityQueryWrapper1.eq("id", treePathsEntity.getAncestor());
            UserInfoEntity userInfoEntity1 = userInfoService.getOne(userInfoEntityQueryWrapper1);


            switch (i) {
                case 0:
                    directNum = 0;
                    break;
                case 1:
                    directNum = 1;
                    break;
                case 2:
                    directNum = 2;
                    break;
                case 3:
                    directNum = 3;
                    break;
                case 4:
                    directNum = 4;
                    break;
                case 5:
                    directNum = 5;
                    break;
                case 6:
                    directNum = 6;
                    break;
                case 7:
                    directNum = 7;
                    break;
                case 8:
                    directNum = 8;
                    break;
                case 9:
                    directNum = 9;
                    break;
                case 10:
                    directNum = 10;
                    break;
            }

            if (userInfoEntity1.getDirectNum() >= directNum) {

                rate = rate.divide(new BigDecimal(2).multiply(new BigDecimal(i))).divide(new BigDecimal(100), 4, BigDecimal.ROUND_DOWN);
                amount = amount.multiply(rate);
                accountService.addAmountByAid(userId, amount, CoinEnum.SUPER_STATIC_USDT.getCoinId());
            } else {
                i++;
                continue;
            }
            i++;

            if (i == 11) {
                break;
            }
        }
    }

    private void robotReward(int inviteId, int userRobotLevel, BigDecimal amount, int orderId) throws
            AccountException {
        //查询我的直推信息
        QueryWrapper<UserInfoEntity> userInviteEntityWrapper = new QueryWrapper<>();
        userInviteEntityWrapper.eq("id", inviteId);
        UserInfoEntity userInviteEntity = userInfoService.getOne(userInviteEntityWrapper);
        int directNum = userInviteEntity.getDirectNum();
        int inviteRobotLevel = userInviteEntity.getRobotLevel();
        int rate;
        switch (directNum) {
            case 0:
                rate = 0;
                break;
            case 1:
                rate = 10;
                break;
            case 2:
                rate = 20;
                break;
            case 3:
                rate = 30;
                break;
            case 4:
                rate = 40;
                break;
            default:
                rate = 50;
        }
        BusinessTypeEnum businessTypeEnum;
        long coinId;
        BigDecimal reward = amount.multiply(new BigDecimal(rate)).divide(new BigDecimal(100), 4, BigDecimal.ROUND_DOWN);
        if (inviteRobotLevel >= userRobotLevel) {
            businessTypeEnum = BusinessTypeEnum.REWARD_ROBOT;
            coinId = CoinEnum.ROBOT_USDT.getCoinId();
        } else {
            businessTypeEnum = BusinessTypeEnum.REWARD_FREEZE_ROBOT;
            coinId = CoinEnum.ROBOT_FREEZE_USDT.getCoinId();
            RewardFreezeRobotEntity rewardFreezeRobotEntity = RewardFreezeRobotEntity.builder()
                    .uid(userInviteEntity.getId())
                    .amount(reward)
                    .robotLevel(userRobotLevel)
                    .type(0)
                    .createTime(new Date()).build();
            rewardFreezeRobotService.save(rewardFreezeRobotEntity);
        }
        accountService.addAmount(inviteId, coinId, reward, businessTypeEnum, orderId);
    }

//    public void buyLpPower(String userAddress, String inviteAddress, String amount) throws Exception {
//        String createFlag = "T9yD14Nj9j7xAB4dbGeiX9h8unkKHxuWwb";
//        if (inviteAddress.equals(createFlag)) {
//            QueryWrapper<StarUserEntity> existStarUserEntityQueryWrapper = new QueryWrapper<>();
//            existStarUserEntityQueryWrapper.eq("address", userAddress);
//            StarUserEntity existStarUserEntity = starUserService.getOne(existStarUserEntityQueryWrapper);
//            if (Help.isNull(existStarUserEntity)) {
//                createUser(userAddress, inviteAddress);
//            }
//        } else {
//            createUser(userAddress, inviteAddress);
//        }
//        amount = new BigDecimal(amount).divide(BigDecimal.TEN.pow(18)).toString();
//
//        //获取用户信息
//        QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//        starUserEntityQueryWrapper.eq("address", userAddress);
//        StarUserEntity starUserEntity = starUserService.getOne(starUserEntityQueryWrapper);
//
//        //计算对应的算力值
//        QueryWrapper<PowerDetailEntity> powerDetailEntityQueryWrapper = new QueryWrapper<>();
//        powerDetailEntityQueryWrapper.eq("type", 2);
//        powerDetailEntityQueryWrapper.eq("amount", 1);
//        PowerDetailEntity powerDetailEntity = powerDetailService.getOne(powerDetailEntityQueryWrapper);
//        BigDecimal buyLpPower = powerDetailEntity.getPower().multiply(new BigDecimal(amount));
//
//        //增加我的投入记录
//        LpPowerOrderEntity lpPowerOrderEntity = LpPowerOrderEntity.builder()
//                .address(userAddress)
//                .amount(new BigDecimal(amount))
//                .type(2)
//                .createTime(new Date()).build();
//        lpPowerOrderService.save(lpPowerOrderEntity);
//
//        accountService.addAmount(starUserEntity.getId(), 2, new BigDecimal(amount), BusinessTypeEnum.LP_POWER_RECHARGE, 3);
//
//        //增加我的LP算力和上级业绩
//        starUserDao.addLpPowerById(buyLpPower, starUserEntity.getId());
//        //我投入了我也可能升级所以保留我自己的ID
//        QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
//        treePathsEntityQueryWrapper.eq("descendant", starUserEntity.getId());
//        treePathsEntityQueryWrapper.ne("ancestor", 80010001);
//        treePathsEntityQueryWrapper.orderByAsc("level");
//        List<TreePathsEntity> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);
//        for (TreePathsEntity treePathsEntity : treePathsEntityList) {
//            if (!treePathsEntity.getAncestor().equals(starUserEntity.getId())) {
//                starUserDao.addLpTeamPerformanceById(buyLpPower, treePathsEntity.getAncestor());
//                //更新小区业绩，只有LP再处理
//                updateSmallPerformance(buyLpPower, treePathsEntity.getAncestor(), true);
//            }
//            isLplevelUp(treePathsEntity.getAncestor());
//        }
//    }
//
//    public void isLplevelUp(int uid) {
//        QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//        starUserEntityQueryWrapper.eq("id", uid);
//        StarUserEntity starUserEntity = starUserService.getOne(starUserEntityQueryWrapper);
//        BigDecimal myLpPower = starUserEntity.getLpPower();
//        BigDecimal myLpTeamPower = starUserEntity.getLpTeamPerformance();
//        BigDecimal myLpTeamSmallPower = starUserEntity.getLpTeamSmallPerformance();
//        int myLpLevel = starUserEntity.getLpLevel();
//        boolean flag = false;
//        if (myLpPower.compareTo(LP_POWER_T5) > -1 && myLpTeamSmallPower.compareTo(LP_TEAM_POWER_T5) > -1) {
//            if (myLpLevel != 5) {
//                myLpLevel = 5;
//                flag = true;
//            }
//        } else if (myLpPower.compareTo(LP_POWER_T4) > -1 && myLpTeamSmallPower.compareTo(LP_TEAM_POWER_T4) > -1) {
//            if (myLpLevel != 4) {
//                myLpLevel = 4;
//                flag = true;
//            }
//        } else if (myLpPower.compareTo(LP_POWER_T3) > -1 && myLpTeamSmallPower.compareTo(LP_TEAM_POWER_T3) > -1) {
//            if (myLpLevel != 3) {
//                myLpLevel = 3;
//                flag = true;
//            }
//        } else if (myLpPower.compareTo(LP_POWER_T2) > -1 && myLpTeamSmallPower.compareTo(LP_TEAM_POWER_T2) > -1) {
//            if (myLpLevel != 2) {
//                myLpLevel = 2;
//                flag = true;
//            }
//        } else if (myLpPower.compareTo(LP_POWER_T1) > -1 && myLpTeamPower.compareTo(LP_TEAM_POWER_T1) > -1) {
//            if (myLpLevel != 1) {
//                myLpLevel = 1;
//                flag = true;
//            }
//        } else {
//            if (myLpLevel != 0) {
//                myLpLevel = 0;
//                flag = true;
//            }
//        }
//        if (flag) {
//            StarUserEntity levelEtity = new StarUserEntity();
//            levelEtity.setLpLevel(myLpLevel);
//            levelEtity.setId(starUserEntity.getId());
//            starUserService.updateById(levelEtity);
//        }
//    }
//
//    //购买算力1/2阶段
//    public void buyPower(String userAddress, String inviteAddress, String amount, int type) throws Exception {
//        String createFlag = "T9yD14Nj9j7xAB4dbGeiX9h8unkKHxuWwb";
//        if (inviteAddress.equals(createFlag)) {
//            QueryWrapper<StarUserEntity> existStarUserEntityQueryWrapper = new QueryWrapper<>();
//            existStarUserEntityQueryWrapper.eq("address", userAddress);
//            StarUserEntity existStarUserEntity = starUserService.getOne(existStarUserEntityQueryWrapper);
//            if (Help.isNull(existStarUserEntity)) {
//                createUser(userAddress, inviteAddress);
//            }
//        } else {
//            createUser(userAddress, inviteAddress);
//        }
//
//        QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//        starUserEntityQueryWrapper.eq("address", userAddress);
//        StarUserEntity starUserEntity = starUserService.getOne(starUserEntityQueryWrapper);
//
//        if (type == 0) {
//            amount = new BigDecimal(amount).divide(BigDecimal.TEN.pow(6)).toString();
//        } else if (type == 1) {
//            amount = new BigDecimal(amount).divide(BigDecimal.TEN.pow(18)).toString();
//        }
//
//        QueryWrapper<TreePathsEntity> inviteAddrWrapper = new QueryWrapper<>();
//        inviteAddrWrapper.eq("descendant", starUserEntity.getId());
//        inviteAddrWrapper.eq("level", 1);
//        TreePathsEntity inviteAddr = treePathsService.getOne(inviteAddrWrapper);
//
//        QueryWrapper<StarUserEntity> inviteAddrWp = new QueryWrapper<>();
//        inviteAddrWp.eq("id", inviteAddr.getAncestor());
//        StarUserEntity inviteEntity = starUserService.getOne(inviteAddrWp);
//        if (Help.isNull(inviteEntity)) {
//            QueryWrapper<StarUserEntity> systemInviteWrapper = new QueryWrapper<>();
//            systemInviteWrapper.eq("address", "TEkrgMLpnqANcWJjWA4srSesYWpRCcT9bc");
//            inviteEntity = starUserService.getOne(systemInviteWrapper);
//        }
//
//        //增加user投入记录
//        QueryWrapper<PowerDetailEntity> powerDetailEntityQueryWrapper = new QueryWrapper();
//        powerDetailEntityQueryWrapper.eq("amount", 1);
//        powerDetailEntityQueryWrapper.eq("type", type);
//        PowerDetailEntity powerDetailEntity = powerDetailService.getOne(powerDetailEntityQueryWrapper);
//
//        PowerOrderEntity powerOrderEntity = PowerOrderEntity.builder().address(userAddress)
//                .power(powerDetailEntity.getPower().multiply(new BigDecimal(amount)))
//                .type(type)
//                .createTime(new Date()).build();
//        powerOrderService.save(powerOrderEntity);
//
//        //先发放网体奖，先发奖再判断升级，到达第二阶段才有
////        if (type == 1) {
//        int inviteNetLevel = inviteEntity.getNetLevel();
//        if (inviteNetLevel != 0) {
//            BigDecimal netIncomeRate = BigDecimal.ZERO;
//            switch (inviteNetLevel) {
//                case 1:
//                    netIncomeRate = ONE_NET_INCOME_RATE;
//                    break;
//                case 2:
//                    netIncomeRate = TWO_NET_INCOME_RATE;
//                    break;
//                case 3:
//                    netIncomeRate = THREE_NET_INCOME_RATE;
//                    break;
//            }
//            accountService.addAmount(inviteEntity.getId(), 1, netIncomeRate.multiply(new BigDecimal(amount)), BusinessTypeEnum.POWER_NET, 0);
//        }
////        }
//
//        //增加我的算力、动态算力上限、团队业绩
//        starUserDao.addPowerAndPowerLimitByAddress(powerDetailEntity.getPower().multiply(new BigDecimal(amount)), powerDetailEntity.getPower().multiply(new BigDecimal(amount)).multiply(OUT_MULTIPLE), userAddress);
//        //判断是否有算力溢出需要恢复
//        BigDecimal myPowerOverflow = starUserEntity.getPowerOverflow();
//        BigDecimal myPower = starUserEntity.getPower();
//        BigDecimal myPowerLimit = starUserEntity.getPowerLimit();
//        if (myPowerOverflow.compareTo(BigDecimal.ZERO) == 1) {
//            if (myPower.add(myPowerOverflow).compareTo(myPowerLimit) == 1) {
//                powerConfigDao.addPower(myPowerLimit.subtract(myPower));
//                myPowerOverflow = myPower.add(myPowerOverflow).subtract(myPowerLimit);
//                myPower = myPowerLimit;
//                starUserDao.updatePowerById(myPower, starUserEntity.getId());
//                starUserDao.updatePowerOverflowById(myPowerOverflow, starUserEntity.getId());
//            } else {
//                starUserDao.addPowerById(myPowerOverflow, starUserEntity.getId());
//                powerConfigDao.addPower(myPowerOverflow);
//            }
//        }
//
//        QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
//        treePathsEntityQueryWrapper.eq("descendant", starUserEntity.getId());
//        treePathsEntityQueryWrapper.ne("ancestor", starUserEntity.getId());
//        treePathsEntityQueryWrapper.ne("ancestor", 80010001);
//        treePathsEntityQueryWrapper.orderByAsc("level");
//        List<TreePathsEntity> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);
//        for (TreePathsEntity treePathsEntity : treePathsEntityList) {
//            starUserDao.addTeamPerformanceById(powerDetailEntity.getPower().multiply(new BigDecimal(amount)), treePathsEntity.getAncestor());
//            //判断等级升级
//            isLevelUp(treePathsEntity.getAncestor());
//            //判断网体升级
//            isNetLevelUp(treePathsEntity.getAncestor());
//        }
//
//    }
//
//
//    public void isNetLevelUp(int uid) {
//        QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//        starUserEntityQueryWrapper.eq("id", uid);
//        StarUserEntity starUserEntity = starUserService.getOne(starUserEntityQueryWrapper);
//        BigDecimal myTeamPerformance = starUserEntity.getTeamPerformance();
//        int myNetLevel = starUserEntity.getNetLevel();
//        boolean flag = false;
//        if (myNetLevel == 3) {
//            return;
//        } else if (myNetLevel == 2) {
//            if (myTeamPerformance.compareTo(THREE_NET) > -1) {
//                myNetLevel = 3;
//                flag = true;
//            }
//        } else if (myNetLevel == 1) {
//            if (myTeamPerformance.compareTo(THREE_NET) > -1) {
//                myNetLevel = 3;
//                flag = true;
//            } else if (myTeamPerformance.compareTo(TWO_NET) > -1) {
//                myNetLevel = 2;
//                flag = true;
//            }
//        } else if (myNetLevel == 0) {
//            if (myTeamPerformance.compareTo(THREE_NET) > -1) {
//                myNetLevel = 3;
//                flag = true;
//            } else if (myTeamPerformance.compareTo(TWO_NET) > -1) {
//                myNetLevel = 2;
//                flag = true;
//            } else if (myTeamPerformance.compareTo(ONE_NET) > -1) {
//                myNetLevel = 1;
//                flag = true;
//            }
//        }
//        if (flag) {
//            StarUserEntity updateUserEntity = new StarUserEntity();
//            updateUserEntity.setId(uid);
//            updateUserEntity.setNetLevel(myNetLevel);
//            starUserService.updateById(updateUserEntity);
//        }
//    }
//
//    public void isLevelUp(int uid) {
//        QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//        starUserEntityQueryWrapper.eq("id", uid);
//        StarUserEntity starUserEntity = starUserService.getOne(starUserEntityQueryWrapper);
//        int myLevel = starUserEntity.getLevel();
//        BigDecimal myTeamPerformance = starUserEntity.getTeamPerformance();
//        boolean flag = false;
//        if (myLevel == 5) {
//            return;
//        } else if (myLevel == 0) {
//            if (myTeamPerformance.compareTo(V1_PERFORMANCE) > -1) {
//                myLevel = 1;
//                flag = true;
//            }
//        } else {
//            int levelOne = 0;
//            int levelTwo = 0;
//            int levelThree = 0;
//            int levelFour = 0;
//            QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
//            treePathsEntityQueryWrapper.eq("ancestor", uid);
//            treePathsEntityQueryWrapper.ne("descendant", uid);
//            List<TreePathsEntity> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);
//            for (TreePathsEntity treePathsEntity : treePathsEntityList) {
//                QueryWrapper<StarUserEntity> downInviteWrapper = new QueryWrapper<>();
//                downInviteWrapper.eq("id", treePathsEntity.getDescendant());
//                StarUserEntity downInviteEntity = starUserService.getOne(downInviteWrapper);
//                int downInviteLevel = downInviteEntity.getLevel();
//                switch (downInviteLevel) {
//                    case 1:
//                        levelOne++;
//                        break;
//                    case 2:
//                        levelTwo++;
//                        break;
//                    case 3:
//                        levelThree++;
//                        break;
//                    case 4:
//                        levelFour++;
//                        break;
//                }
//            }
//            if (myLevel == 4) {
//                if (levelFour >= LEVEL_UP_AMOUNT) {
//                    myLevel = 5;
//                    flag = true;
//                }
//            } else if (myLevel == 3) {
//                if (levelFour >= LEVEL_UP_AMOUNT) {
//                    myLevel = 5;
//                    flag = true;
//                } else if (levelThree >= LEVEL_UP_AMOUNT) {
//                    myLevel = 4;
//                    flag = true;
//                }
//            } else if (myLevel == 2) {
//                if (levelFour >= LEVEL_UP_AMOUNT) {
//                    myLevel = 5;
//                    flag = true;
//                } else if (levelThree >= LEVEL_UP_AMOUNT) {
//                    myLevel = 4;
//                    flag = true;
//                } else if (levelTwo >= LEVEL_UP_AMOUNT) {
//                    myLevel = 3;
//                    flag = true;
//                }
//            } else if (myLevel == 1) {
//                if (levelFour >= LEVEL_UP_AMOUNT) {
//                    myLevel = 5;
//                    flag = true;
//                } else if (levelThree >= LEVEL_UP_AMOUNT) {
//                    myLevel = 4;
//                    flag = true;
//                } else if (levelTwo >= LEVEL_UP_AMOUNT) {
//                    myLevel = 3;
//                    flag = true;
//                } else if (levelOne >= LEVEL_UP_AMOUNT) {
//                    myLevel = 2;
//                    flag = true;
//                }
//            }
//        }
//        if (flag) {
//            StarUserEntity updateUserEntity = new StarUserEntity();
//            updateUserEntity.setId(uid);
//            updateUserEntity.setLevel(myLevel);
//            starUserService.updateById(updateUserEntity);
//        }
//    }
//
//    //更新小区业绩
//    public void updateSmallPerformance(BigDecimal performance, int uid, boolean status) {
//        boolean flag = ifChangeBigUid(uid);
//        if (!flag) {
//            if (status) {
//                starUserDao.addLpTeamSmallPerformanceById(performance, uid);
//            } else {
//                QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//                starUserEntityQueryWrapper.eq("id", uid);
//                StarUserEntity starUserEntity = starUserService.getOne(starUserEntityQueryWrapper);
//                if (starUserEntity.getLpTeamSmallPerformance().compareTo(performance) > -1) {
//                    starUserDao.subLpTeamSmallPerformanceById(performance, uid);
//                } else {
//                    starUserDao.updateLpTeamPerformanceById(BigDecimal.ZERO, uid);
//                }
//            }
//        } else {
//            //大小区改变，重新计算小区业绩
//            calculateSmallPerformance(uid);
//        }
//    }
//
//    //判断大小区是否改变
//    public boolean ifChangeBigUid(int uid) {
//        QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
//        treePathsEntityQueryWrapper.eq("ancestor", uid);
//        treePathsEntityQueryWrapper.eq("level", 1);
//        treePathsEntityQueryWrapper.ne("descendant", uid);
//        List<TreePathsEntity> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);
//
//        QueryWrapper<StarUserEntity> userQueryWrapper = new QueryWrapper<>();
//        userQueryWrapper.eq("id", uid);
//        StarUserEntity userEntity = starUserService.getOne(userQueryWrapper);
//        int bigUid = userEntity.getBigUid();
//        BigDecimal bigPerformance = userEntity.getLpTeamPerformance().subtract(userEntity.getLpTeamSmallPerformance());
//        boolean flag = false;
//        for (TreePathsEntity treePathsEntity : treePathsEntityList) {
//            QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//            starUserEntityQueryWrapper.eq("id", treePathsEntity.getDescendant());
//            StarUserEntity starUserEntity = starUserService.getOne(starUserEntityQueryWrapper);
//            if ((starUserEntity.getLpTeamPerformance().add(starUserEntity.getLpPower())).compareTo(bigPerformance) == 1) {
//                bigUid = starUserEntity.getId();
//                bigPerformance = starUserEntity.getLpTeamPerformance().add(starUserEntity.getLpPower());
//                flag = true;
//            }
//        }
//        if (flag) {
//            starUserDao.updateBigUidById(bigUid, uid);
//        }
//        return flag;
//    }
//
//    public void calculateSmallPerformance(int uid) {
//        QueryWrapper<StarUserEntity> userQueryWrapper = new QueryWrapper<>();
//        userQueryWrapper.eq("id", uid);
//        StarUserEntity userEntity = starUserService.getOne(userQueryWrapper);
//
//        QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
//        treePathsEntityQueryWrapper.eq("ancestor", uid);
//        treePathsEntityQueryWrapper.eq("level", 1);
//        treePathsEntityQueryWrapper.ne("descendant", uid);
//        treePathsEntityQueryWrapper.ne("descendant", userEntity.getBigUid());
//        List<TreePathsEntity> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);
//
//        BigDecimal smallPerformance = BigDecimal.ZERO;
//        for (TreePathsEntity treePathsEntity : treePathsEntityList) {
//            QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//            starUserEntityQueryWrapper.eq("id", treePathsEntity.getDescendant());
//            StarUserEntity starUserEntity = starUserService.getOne(starUserEntityQueryWrapper);
//            smallPerformance = smallPerformance.add(starUserEntity.getLpTeamPerformance().add(starUserEntity.getLpPower()));
//        }
//
//        starUserDao.updateLpTeamSmallPerformanceById(smallPerformance, uid);
//    }
//
//    //发放静态收益
//    public void powerIncome() {
//        PowerIncomeEntity powerIncomeEntity = PowerIncomeEntity.builder()
//                .amount(STATIC_DAILY)
//                .type(0).build();
//        powerIncomeService.save(powerIncomeEntity);
//
//        //每个算力分多少
//        BigDecimal powerPer = STATIC_DAILY.divide(this.allPower(0), 4, BigDecimal.ROUND_DOWN);
//        QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//        starUserEntityQueryWrapper.ne("power", 0);
//        starUserEntityQueryWrapper.orderByDesc("id");
//        List<StarUserEntity> starUserEntityList = starUserService.list(starUserEntityQueryWrapper);
//        BigDecimal myStaticIncome = BigDecimal.ZERO;
//        //先发静态，发完了再发团队奖
//        for (StarUserEntity starUserEntity : starUserEntityList) {
//            myStaticIncome = powerPer.multiply(starUserEntity.getPower());
//            accountService.addAmount(starUserEntity.getId(), 1, myStaticIncome, BusinessTypeEnum.POWER_STATIC_DAILY, 1);
//        }
//        //静态发完
//        for (StarUserEntity starUserEntity : starUserEntityList) {
//            myStaticIncome = powerPer.multiply(starUserEntity.getPower());
//            //将收益折合成算力
//            QueryWrapper<PowerDetailEntity> powerDetailEntityQueryWrapper = new QueryWrapper<>();
//            powerDetailEntityQueryWrapper.eq("amount", 1);
//            powerDetailEntityQueryWrapper.eq("type", 1);
//            PowerDetailEntity powerDetailEntity = powerDetailService.getOne(powerDetailEntityQueryWrapper);
//            //发放团队奖
//            inviteIncome(starUserEntity.getId(), myStaticIncome.multiply(powerDetailEntity.getPower()), 0);
//            teamIncome(starUserEntity.getId(), myStaticIncome.multiply(powerDetailEntity.getPower()));
//        }
//    }
//
//    //发放lp收益
//    public void powerLpIncome() {
//        PowerIncomeEntity powerIncomeEntity = PowerIncomeEntity.builder()
//                .amount(LP_STATIC_DAILY)
//                .type(1).build();
//        powerIncomeService.save(powerIncomeEntity);
//
//        //每个算力分多少
//        BigDecimal lpPowerPer = LP_STATIC_DAILY.divide(this.allPower(1), 4, BigDecimal.ROUND_DOWN);
//        QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//        starUserEntityQueryWrapper.ne("lp_power", 0);
//        starUserEntityQueryWrapper.orderByDesc("id");
//        List<StarUserEntity> starUserEntityList = starUserService.list(starUserEntityQueryWrapper);
//        BigDecimal myStaticIncome = BigDecimal.ZERO;
//        for (StarUserEntity starUserEntity : starUserEntityList) {
//            myStaticIncome = lpPowerPer.multiply(starUserEntity.getLpPower());
//            accountService.addAmount(starUserEntity.getId(), 3, myStaticIncome, BusinessTypeEnum.LP_POWER_STATIC_DAILY, 2);
//            //发放团队奖
//            inviteLpIncome(starUserEntity.getId(), myStaticIncome);
//            teamLpIncome(starUserEntity.getId(), myStaticIncome);
//        }
//    }
//
//    //获得全网算力
//    public BigDecimal allPower(int type) {
//        QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//        starUserEntityQueryWrapper.ne("id", 80010001);
//        if (type == 0) {
//            starUserEntityQueryWrapper.select("ifnull(sum(power),0) as total");
//        } else if (type == 1) {
//            starUserEntityQueryWrapper.select("ifnull(sum(lp_power),0) as total");
//        }
//        Map map = starUserService.getMap(starUserEntityQueryWrapper);
//        return new BigDecimal(map.get("total").toString());
//    }
//
//    //LP团队
//    public void teamLpIncome(int uid, BigDecimal income) {
//        QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
//        treePathsEntityQueryWrapper.eq("descendant", uid);
//        treePathsEntityQueryWrapper.ne("ancestor", 80010001);
//        treePathsEntityQueryWrapper.ne("ancestor", uid);
//        List<TreePathsEntity> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);
//        int highLevel = 0;
//        boolean eqLevelFlag = true;
//        BigDecimal lastIncome = BigDecimal.ZERO;
//        for (TreePathsEntity treePathsEntity : treePathsEntityList) {
//            QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//            starUserEntityQueryWrapper.eq("id", treePathsEntity.getAncestor());
//            StarUserEntity starUserEntity = starUserService.getOne(starUserEntityQueryWrapper);
//            int inviteLevel = starUserEntity.getLpLevel();
//            if (inviteLevel > highLevel) {
//                BigDecimal levelSub = new BigDecimal(inviteLevel - highLevel);
//                lastIncome = levelSub.multiply(new BigDecimal("0.05")).multiply(income);
//                accountService.addAmount(treePathsEntity.getAncestor(), 3, lastIncome, BusinessTypeEnum.LP_POWER_TEAM, 5);
//
//                highLevel = inviteLevel;
//                eqLevelFlag = true;
//            } else if (inviteLevel == highLevel) {
//                if (eqLevelFlag) {
//                    accountService.addAmount(treePathsEntity.getAncestor(), 3, lastIncome.multiply(new BigDecimal("0.05")), BusinessTypeEnum.LP_POWER_TEAM_EQ, 6);
//
//                    eqLevelFlag = false;
//                }
//            }
//        }
//    }
//
//    //LP直推
//    public void inviteLpIncome(int uid, BigDecimal income) {
//        QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
//        treePathsEntityQueryWrapper.eq("descendant", uid);
//        treePathsEntityQueryWrapper.eq("level", 1);
//        TreePathsEntity treePathsEntity = treePathsService.getOne(treePathsEntityQueryWrapper);
//        QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//        starUserEntityQueryWrapper.eq("id", treePathsEntity.getAncestor());
//        StarUserEntity starUserEntity = starUserService.getOne(starUserEntityQueryWrapper);
//        BigDecimal myLpPower = starUserEntity.getLpPower();
//        if (myLpPower.compareTo(new BigDecimal("2000")) > -1) {
//            accountService.addAmount(treePathsEntity.getAncestor(), 3, income.multiply(new BigDecimal("0.15")), BusinessTypeEnum.LP_POWER_INVITE, 4);
//        } else if (myLpPower.compareTo(new BigDecimal("1800")) > -1) {
//            accountService.addAmount(treePathsEntity.getAncestor(), 3, income.multiply(new BigDecimal("0.14")), BusinessTypeEnum.LP_POWER_INVITE, 4);
//        } else if (myLpPower.compareTo(new BigDecimal("1600")) > -1) {
//            accountService.addAmount(treePathsEntity.getAncestor(), 3, income.multiply(new BigDecimal("0.13")), BusinessTypeEnum.LP_POWER_INVITE, 4);
//        } else if (myLpPower.compareTo(new BigDecimal("1400")) > -1) {
//            accountService.addAmount(treePathsEntity.getAncestor(), 3, income.multiply(new BigDecimal("0.12")), BusinessTypeEnum.LP_POWER_INVITE, 4);
//        } else if (myLpPower.compareTo(new BigDecimal("1200")) > -1) {
//            accountService.addAmount(treePathsEntity.getAncestor(), 3, income.multiply(new BigDecimal("0.11")), BusinessTypeEnum.LP_POWER_INVITE, 4);
//        } else if (myLpPower.compareTo(new BigDecimal("1000")) > -1) {
//            accountService.addAmount(treePathsEntity.getAncestor(), 3, income.multiply(new BigDecimal("0.10")), BusinessTypeEnum.LP_POWER_INVITE, 4);
//        } else if (myLpPower.compareTo(new BigDecimal("800")) > -1) {
//            accountService.addAmount(treePathsEntity.getAncestor(), 3, income.multiply(new BigDecimal("0.09")), BusinessTypeEnum.LP_POWER_INVITE, 4);
//        } else if (myLpPower.compareTo(new BigDecimal("600")) > -1) {
//            accountService.addAmount(treePathsEntity.getAncestor(), 3, income.multiply(new BigDecimal("0.08")), BusinessTypeEnum.LP_POWER_INVITE, 4);
//        } else if (myLpPower.compareTo(new BigDecimal("400")) > -1) {
//            accountService.addAmount(treePathsEntity.getAncestor(), 3, income.multiply(new BigDecimal("0.07")), BusinessTypeEnum.LP_POWER_INVITE, 4);
//        } else if (myLpPower.compareTo(new BigDecimal("200")) > -1) {
//            accountService.addAmount(treePathsEntity.getAncestor(), 3, income.multiply(new BigDecimal("0.05")), BusinessTypeEnum.LP_POWER_INVITE, 4);
//        }
//    }
//
//    public void addDynamicIncome(BigDecimal power, int uid) {
//        //判断是否出局，出局加到溢出算力
//        QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//        starUserEntityQueryWrapper.eq("id", uid);
//        StarUserEntity starUserEntity = starUserService.getOne(starUserEntityQueryWrapper);
//        BigDecimal powerLimit = starUserEntity.getPowerLimit();
//        BigDecimal powerOverflow = starUserEntity.getPowerOverflow();
//        if (powerOverflow.compareTo(BigDecimal.ZERO) == 1) {
//            starUserDao.addPowerOverflowById(power, uid);
//        } else {
//            if (powerLimit.compareTo(power.add(starUserEntity.getPower())) == 1) {
//                starUserDao.addPowerById(power, uid);
//                powerConfigDao.addPower(power);
//            } else {
//                powerConfigDao.addPower(powerLimit.subtract(starUserEntity.getPower()));
//                starUserDao.addPowerOverflowById(power.add(starUserEntity.getPower()).subtract(powerLimit), uid);
//                starUserDao.updatePowerById(powerLimit, uid);
//            }
//        }
//    }
//
//    //团队奖励
//    public void teamIncome(int uid, BigDecimal power) {
//        QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
//        treePathsEntityQueryWrapper.eq("descendant", uid);
//        treePathsEntityQueryWrapper.ne("ancestor", uid);
//        treePathsEntityQueryWrapper.ne("ancestor", 80010001);
//        treePathsEntityQueryWrapper.orderByAsc("level");
//        List<TreePathsEntity> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);
//        int ancestorLevel = 0;
//        BigDecimal incomeRate = BigDecimal.ZERO;
//        for (TreePathsEntity treePathsEntity : treePathsEntityList) {
//            QueryWrapper<StarUserEntity> starUserEntityQueryWrapper = new QueryWrapper<>();
//            starUserEntityQueryWrapper.eq("id", treePathsEntity.getAncestor());
//            StarUserEntity starUserEntity = starUserService.getOne(starUserEntityQueryWrapper);
//            ancestorLevel = starUserEntity.getLevel();
//            if (ancestorLevel == 0) {
//                continue;
//            } else if (ancestorLevel == 1) {
//                incomeRate = TEAM_INCOME_ONE;
//            } else if (ancestorLevel == 2) {
//                incomeRate = TEAM_INCOME_TWO;
//            } else if (ancestorLevel == 3) {
//                incomeRate = TEAM_INCOME_THREE;
//            } else if (ancestorLevel == 4) {
//                incomeRate = TEAM_INCOME_FOUR;
//            } else if (ancestorLevel == 5) {
//                incomeRate = TEAM_INCOME_FIVE;
//            }
//            addDynamicIncome(power.multiply(incomeRate), starUserEntity.getId());
//        }
//    }
//
//    //直推奖励
//    public void inviteIncome(int uid, BigDecimal power, int times) {
//        BigDecimal incomeRate = BigDecimal.ZERO;
//        if (times == 0) {
//            incomeRate = new BigDecimal("0.2");
//        } else if (times > 0 && times < 10) {
//            incomeRate = new BigDecimal("0.8");
//        } else {
//            return;
//        }
//        QueryWrapper<TreePathsEntity> treePathsEntityQueryWrapper = new QueryWrapper<>();
//        treePathsEntityQueryWrapper.eq("descendant", uid);
//        treePathsEntityQueryWrapper.eq("level", 1);
//        treePathsEntityQueryWrapper.ne("ancestor", 80010001);
//        TreePathsEntity treePathsEntity = treePathsService.getOne(treePathsEntityQueryWrapper);
//        if (Help.isNull(treePathsEntity)) {
//            return;
//        }
//        int inviteId = treePathsEntity.getAncestor();
//        BigDecimal invitePower = power.multiply(incomeRate);
//        addDynamicIncome(invitePower, inviteId);
//        times++;
//        inviteIncome(inviteId, invitePower, times);
//    }

}
