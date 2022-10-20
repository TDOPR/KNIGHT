// SPDX-License-Identifier: MIT

contract NineNineToOne is Ownable {

    using SafeMath for uint256;
    using SafeMath for uint8;
    using SafeERC20 for IERC20;

    mapping(uint => DataSets.usersInfo) public usersInfo;
    mapping(address => uint) public idToAddress;
    mapping(address => bool) public userIsRegi;
    mapping(address => address[]) public recommendUserAmount;
    mapping(address => address[]) public buyBotusers;
    mapping(address => bool) public buyBot;

    
    uint256[10] public levelRate;
    uint256[10] public levelRecommend;
    uint256[6] public level;
    uint256[3] public botLevel;
    uint256[3] public invVal;
    address[] public recommendUser;
    address[] public buyBotUsers;
    uint256 public tvl;

    uint256 public blockAmountWeek = 28800 * 7;

    uint256 performance = 30000 ether;
    uint256 botRefer = 10;

    uint256 public nPlayerNum;

    uint256[3] private emptyArr;

    IERC20 public usdtToken = IERC20(0xC0D138c80730b4eef8B82525e3841aB9e86cf463);

    event BuyLevel(address indexed usersInfo, uint256 indexed level);
    event BuyBot(address indexed usersInfo, uint256 indexed level);
    event Withdraw(address indexed usersInfo, uint256 indexed level);
    event RewardPaid(address indexed userAddress, uint256 amount);

    constructor() public {

        level[0] = 0;
        level[1] = 1;
        level[2] = 2;
        level[3] = 3;
        level[4] = 4;
        level[5] = 5;

        botLevel[0] = 0;
        botLevel[1] = 300;
        botLevel[2] = 500;
        botLevel[3] = 3000;

        invVal[0] = 5000;
        invVal[1] = 5000;
        invVal[2] = 10000;
        invVal[3] = 50000;

        intrRate[0] = 0;
        intrRate[1] = 7;
        intrRate[2] = 12;
        intrRate[3] = 17;
        intrRate[4] = 22;
        intrRate[5] = 27;

    }



    function setPrice(uint256 value) public onlyOwner {
        _priceOn = true;
        _price = value;
    }

    function deposit(uint256 value, address _agent) public {
        address _addr = msg.sender;
        uint256 depositTime = block.number;
        require(value >= 10, "value insuffcient");
        require(usdtToken.balanceOf(_addr) >= _amount, "USDT Insufficient balance");

        if (userIsRegi[_addr]) {
            createPlayer(_addr, _agent);
        }

        usdtToken.safeTransferFrom(msg.sender, address(this), value);
        depositValue[_addr] += value;
        usersInfo[idToAddress[_addr]].allBuy += value;
        usersInfo[idToAddress[_addr]].lastBlock = depositTime;
        tvl += value;

        updatePerformanceAndTeamNumber(userRegiFlag, idToAddress[userAddress], amount);
        bonusToTeam(idToAddress[userAddress], amount);
     
    }

    function buyBot(uint256 value, uint256 level, address referrer) public {
        require(usersInfo[idToAddress[_addr]].botlevel < level, "can not buy this level");
        require(userIsRegi[referrer], "referrer is not register");
        uint256 _amount;
        address _addr = msg.sender;
        _amount = botLevelVal[level] - botLevelVal[usersInfo[idToAddress[_addr]].botlevel];
        require(usdtToken.balanceOf(_addr) >= _amount, "USDT insufficient balance");
        usdtToken.safeTransferFrom(msg.sender, address(this), value);
        if (usersInfo[idToAddress[_addr]].botlevel == 0) {
            buyBotusers[usersInfo[idToAddress[_addr]].referrer].push(_addr);
            uint256 a = buyBotusers[usersInfo[idToAddress[_addr]].referrer].length;
            if (a >= 6) {
                usersInfo[idToAddress[_addr]].pendValue += _amount.mul(50).div(100);
            } else if (a <= 5) {
                usersInfo[idToAddress[_addr]].pendValue += _amount.mul(a).div(100);
            }
            usersInfo[idToAddress[_addr]].buyBot == true;
        }
        usersInfo[idToAddress[_addr]].botLevel = level;
        usersInfo[idToAddress[_addr]].investVal = invVal[level];
    }

    function withdraw(uint256 value) public {
        address _addr = msg.sender;
        uint256 currentTime = block.number;
        uint256 elapsedTime = currentTime - usersInfo[idToAddress[_addr]].lastBlock;
        uint256 liquidatedVal;
        if (elapsedTime < blockAmountWeek) {
            liquidatedVal = usersInfo[idToAddress[_addr]].allBuy.add(interest).mul(2).div(100);
        }
        uint256 fee;
        require(value <= usersInfo[idToAddress[_addr]].allBuy, "value insufficient");
        fee = value.mul(fee).div(100);
        if (fee <= 1) {
            fee = 1;
        }
        uint256 withDrawVal = value.sub(fee).sub(liquidatedVal);
        usdtToken.safeTransferFrom(address(this), msg.sender, withDrawVal);
        usersInfo[idToAddress[_addr]].allBuy -= value;
    }

    function createPlayer(address _addr, address _agent) public {
        if (idToAddress[_addr] == 0) {
            require(userIsRegi[_agent], "recommend is not register!");
            nPlayerNum++;
            idToAddress[_addr] = nPlayerNum;
            usersInfo[nPlayerNum] = DataSets.usersInfo(_addr, 0, 0, 0, 0, 0, 0, emptyArr, 0, 0, 0);
            userIsRegi[_addr] = true;

            if (_addr != _agent) {
                usersInfo[nPlayerNum].referrerId = idToAddress[_agent];
                usersInfo[idToAddress[_agent]].recommends++;
            }
        }
    }

    function updatePerformanceAndTeamNumber(bool newUserFlag, uint256 userId, uint256 amount) private {
        uint256 referrerId = userId;
        for (uint i = 0; i <= 10; i++) {
            if (referrerId == 0) {
                break;
            }
            usersInfo[referrerId].performance += amount;
            if (i != 0 && newUserFlag) {
                usersInfo[referrerId].teamNumber++;
            }

            levelUp(referrerId);
            referrerId = usersInfo[referrerId].referrerId;
        }
    }

    function upDownPerformanceAndTeamNumber(bool newUserFlag, uint256 userId, uint256 amount) private {
        uint256 referrerId = userId;
        for (uint i = 0; i <= 10; i++) {
            if (referrerId == 0) {
                break;
            }
            usersInfo[referrerId].performance -= amount;

            levelDown(referrerId);
            referrerId = usersInfo[referrerId].referrerId;
        }
    }

    function levelUp(uint256 _uid) private {
        uint256 agentId = playerxID_[_uid].agent;
        uint256 myLevel = playerxID_[_uid].level;
        address myAddr = playerxID_[_uid].addr;
        if (myLevel == 4) {
            if (playerxID_[_uid].levelIdAddr[4] >= 2) {
                playerxID_[_uid].level = 5;
            }
        } else if (myLevel == 3) {
            if (playerxID_[_uid].levelIdAddr[4] >= 2) {
                playerxID_[_uid].level = 5;
            } else if (playerxID_[_uid].levelIdAddr[3] >= 2) {
                playerxID_[_uid].level = 4;
                playerxID_[agentId].levelIdAddr[4] += 1;
                levelUp(agentId);
            }
        } else if (myLevel == 2) {
            if (playerxID_[_uid].levelIdAddr[4] >= 2) {
                playerxID_[_uid].level = 5;
            } else if (playerxID_[_uid].levelIdAddr[3] >= 2) {
                playerxID_[_uid].level = 4;
                playerxID_[agentId].levelIdAddr[4] += 1;
                levelUp(agentId);
            } else if (playerxID_[_uid].levelIdAddr[2] >= 2) {
                playerxID_[_uid].level = 3;
                playerxID_[agentId].levelIdAddr[3] += 1;
                levelUp(agentId);
            }
        } else if (myLevel == 1) {
            if (playerxID_[_uid].levelIdAddr[4] >= 2) {
                playerxID_[_uid].level = 5;
            } else if (playerxID_[_uid].levelIdAddr[3] >= 2) {
                playerxID_[_uid].level = 4;
                playerxID_[agentId].levelIdAddr[4] += 1;
                levelUp(agentId);
            } else if (playerxID_[_uid].levelIdAddr[2] >= 2) {
                playerxID_[_uid].level = 3;
                playerxID_[agentId].levelIdAddr[3] += 1;
                levelUp(agentId);
            } else if (playerxID_[_uid].levelIdAddr[2] >= 2) {
                playerxID_[_uid].level = 2;
                playerxID_[agentId].levelIdAddr[2] += 1;
                levelUp(agentId);
            } 
        } else if (myLevel == 0) {
            if (playerxID_[_uid].levelIdAddr[4] >= 2) {
                playerxID_[_uid].level = 5;
            } else if (playerxID_[_uid].levelIdAddr[3] >= 2) {
                playerxID_[_uid].level = 4;
                playerxID_[agentId].levelIdAddr[4] += 1;
                levelUp(agentId);
            } else if (playerxID_[_uid].levelIdAddr[2] >= 2) {
                playerxID_[_uid].level = 3;
                playerxID_[agentId].levelIdAddr[3] += 1;
                levelUp(agentId);
            } else if (playerxID_[_uid].levelIdAddr[2] >= 2) {
                playerxID_[_uid].level = 2;
                playerxID_[agentId].levelIdAddr[2] += 1;
                levelUp(agentId);
            } else {
                if (playerxID_[_uid].teamPerformance >= performance
                && playerxID_[_uid].botRecommends >= botRefer) {
                    playerxID_[_uid].level = 1;
                    playerxID_[agentId].levelIdAddr[1] += 1;
                    levelUp(agentId);
                }
            }
        }
    }

    function levelDown(uint256 _uid) private {
        uint256 agentId = playerxID_[_uid].agent;
        uint256 myLevel = playerxID_[_uid].level;

        if (myLevel == 1) {
            if (playerxID_[_uid].teamPerformance < performance) {
                playerxID_[_uid].level = 0;
                levelDown(agentId);
            }
        } else if (myLevel == 2) {
            if (playerxID_[_uid].levelIdAddr[1] < 2 && playerxID_[_uid].teamPerformance >= performance) {
                playerxID_[_uid].level = 1;
                levelDown(agentId);
            } else if (playerxID_[_uid].teamPerformance < performance) {
                playerxID_[_uid].level = 0;
                levelDown(agentId);
            }
        } else if (myLevel == 3) {
            if (playerxID_[_uid].levelIdAddr[2] < 2) {
                playerxID_[_uid].level = 2;
                levelDown(agentId);
            } else if (playerxID_[_uid].levelIdAddr[1] < 2) {
                playerxID_[_uid].level = 1;
                levelDown(agentId);
            } else if (playerxID_[_uid].teamPerformance < performance) {
                playerxID_[_uid].level = 0;
                levelDown(agentId);
            }
        } else if (myLevel == 4) {
            if (playerxID_[_uid].levelIdAddr[3] < 2 && playerxID_[_uid].levelIdAddr[2] >= 2) {
                playerxID_[_uid].level = 3;
                levelDown(agentId);
            } else if (playerxID_[_uid].levelIdAddr[2] < 2 && playerxID_[_uid].levelIdAddr[1] >= 2) {
                playerxID_[_uid].level = 2;
                levelDown(agentId);
            } else if (playerxID_[_uid].levelIdAddr[1] < 2 && playerxID_[_uid].teamPerformance >= performance) {
                playerxID_[_uid].level = 1;
                levelDown(agentId);
            } else if (playerxID_[_uid].teamPerformance < performance) {
                playerxID_[_uid].level = 0;
                levelDown(agentId);
            }
        } else if (myLevel == 5) {
            if (playerxID_[_uid].levelIdAddr[4] < 2 && playerxID_[_uid].levelIdAddr[3] >= 2) {
                playerxID_[_uid].level = 4;
                levelDown(agentId);
            } else if (playerxID_[_uid].levelIdAddr[3] < 2 && playerxID_[_uid].levelIdAddr[2] >= 2) {
                playerxID_[_uid].level = 3;
                levelDown(agentId);
            } else if (playerxID_[_uid].levelIdAddr[2] < 2 && playerxID_[_uid].levelIdAddr[1] >= 2) {
                playerxID_[_uid].level = 2;
                levelDown(agentId);
            } else if (playerxID_[_uid].levelIdAddr[1] < 2 && playerxID_[_uid].teamPerformance >= performance) {
                playerxID_[_uid].level = 1;
                levelDown(agentId);
            } else if (playerxID_[_uid].teamPerformance < performance) {
                playerxID_[_uid].level = 0;
                levelDown(agentId);
            }
        }
    }

    function getRandomRate() view private returns (uint256){
        uint256 randomNumber = uint256(keccak256(abi.encodePacked(
                (block.timestamp).add
                (block.difficulty).add
                ((uint256(keccak256(abi.encodePacked(block.coinbase)))) / (now)).add
                (block.gaslimit).add
                ((uint256(keccak256(abi.encodePacked(msg.sender)))) / (now)).add
                (block.number)
            ))) % 100;
        return randomNumber;
    }

    function getRate() view private returns (uint256){
        uint256 randomNumber = getRandomRate();
        uint256 interestRate;
        if (randomNumber >= 0 && randomNumber <= 69) {
            interestRate = 5;
        } else if (randomNumber >= 70 && randomNumber <= 79) {
            interestRate = 6;
        } else if (randomNumber >= 80 && randomNumber <= 89) {
            interestRate = 7;
        } else if (randomNumber >= 90 && randomNumber <= 91) {
            interestRate = 8;
        } else if (randomNumber >= 92 && randomNumber <= 93) {
            interestRate = 9;
        } else if (randomNumber >= 94 && randomNumber <= 95) {
            interestRate = 10;
        } else if (randomNumber >= 96 && randomNumber <= 97) {
            interestRate = 11;
        } else if (randomNumber == 98) {
            interestRate = 12;
        } else if (randomNumber == 99) {
            interestRate = 13;
        }
        return interestRate;
    }

    function bonusToTeam(uint256 userId, uint256 amount) private {
        uint256 rate;
        uint256 referrerId = usersInfo[userId].referrerId;
        uint256 referrerLevel = usersInfo[referrerId].level;
        bool teamBonusFlag = false;
        uint256 lastLevel = 0;
        uint256 lastRate = 0;

        for (uint i = 0; i < 10; i++) {
            if (referrerId == 0) {
                break;
            }
            if (usersInfo[referrerId].level > lastLevel) {
                teamBonusFlag = true;
                rate = levelRate[usersInfo[referrerId].level];
            }
            if (teamBonusFlag) {
                usersInfo[referrerId].teamBonus += amount.mul(rate.sub(lastRate)).div(100);
                uint256 usersAmount = amount.mul(rate.sub(lastRate)).div(100);
                usdtToken.safeTransfer(usersInfo[referrerId].addr, usersAmount);
                bankAmount -= amount.mul(rate.sub(lastRate)).div(100);
                lastLevel = referrerLevel;
                lastRate = rate;
            }

            referrerId = usersInfo[referrerId].referrerId;
            teamBonusFlag = false;
        }
    }

    function getRecommendUser(address userAddress) public view returns (address[] memory) {
      
        return recommendUserAmount[userAddress];
    }

}

library DataSets {
    struct usersInfo {
        address addr;
        uint256 botLevel;
        bool buyBot;
        uint256 allBuy;
        uint256 referrerId;
        uint256 Level;
        uint256 allRecommends;
        uint256 botRecommends;
        uint256 performance;
        uint256[4] levelIdAddr;
        uint256 teamPerformance;
        uint256 teamNumber;
        uint256 allBonus;
    }
}