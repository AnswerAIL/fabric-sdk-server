# Fabric-SDK-Java 改进版项目 answer-fabric-sdk
  - [x] **适用对象**: 入门 Hyperledger-Fabric_release-1.0 Java版SDK
  
  - [x] **测试用例**: APITest | BlockChainTest | InvokeTest | QueryTest | UpgradeTest | JoinPeerTest
    - [x] **全流程** [APITest](https://github.com/AnswerCoder/answer-fabric-sdk/blob/master/src/test/java/com/hyperledger/fabric/sdk/handler/APITest.java)
    - [x] **查询块|账本信息** [BlockChainTest](https://github.com/AnswerCoder/answer-fabric-sdk/blob/master/src/test/java/com/hyperledger/fabric/sdk/handler/BlockChainTest.java)
    - [x] **转账操作** [InvokeTest](https://github.com/AnswerCoder/answer-fabric-sdk/blob/master/src/test/java/com/hyperledger/fabric/sdk/handler/InvokeTest.java)
    - [x] **查询智能合约** [QueryTest](https://github.com/AnswerCoder/answer-fabric-sdk/blob/master/src/test/java/com/hyperledger/fabric/sdk/handler/QueryTest.java)
    - [x] **升级智能合约** [UpgradeTest](https://github.com/AnswerCoder/answer-fabric-sdk/blob/master/src/test/java/com/hyperledger/fabric/sdk/handler/UpgradeTest.java)
    - [x] **加入新节点** [JoinPeerTest](https://github.com/AnswerCoder/answer-fabric-sdk/blob/master/src/test/java/com/hyperledger/fabric/sdk/handler/JoinPeerTest.java)
    
  - [x] **测试用例操作顺序建议**: APITest -> JoinPeerTest -> QueryTest -> InvokeTest -> QueryTest -> UpgradeTest -> QueryTest
    - [x] **日志输出模板** [测试案例日志输出.md](https://github.com/AnswerCoder/answer-fabric-sdk/blob/master/%E6%B5%8B%E8%AF%95%E6%A1%88%E4%BE%8B%E6%97%A5%E5%BF%97%E8%BE%93%E5%87%BA.md)
    - [x] **使用前置条件**
        - [x] **1.** 测试前请先配置好服务器信息:  [Config](https://github.com/AnswerCoder/answer-fabric-sdk/blob/master/src/test/java/com/hyperledger/fabric/sdk/common/Config.java)
        - [x] **2.** 使用Redis缓存, 请先安装好redis并配置Redis服务信息 [Constants](https://github.com/AnswerCoder/answer-fabric-sdk/blob/master/src/main/java/com/hyperledger/fabric/sdk/common/Constants.java) 类
        - [x] **3.** channel-artifacts 和 crypto-config 目录下替换自己的通道配置及加密文件, 如果不是很熟, 请直接把 examples/e2e_cli 下的 channel-artifacts 和 crypto-config 拷过来替换掉. <br>
                        (注意放在test下的resources目录, 测试用例都是放在test下的)
  
  - [x] **Redis缓存使用**: 如需使用缓存请先安装Redis, 配置服务信息请参见: [Constants](https://github.com/AnswerCoder/answer-fabric-sdk/blob/master/src/main/java/com/hyperledger/fabric/sdk/common/Constants.java) 类
        
  - [x] **对应Fabric版本**: Hyperledger-Fabric_release-1.0  [Hyperledger-Fabric Link](https://github.com/hyperledger/fabric)
  
  - [x] **官方SDK**: [hyperledger](https://github.com/hyperledger)
    - [x] **fabric-sdk-java**: [fabric-sdk-java](https://github.com/hyperledger/fabric-sdk-java)
    - [x] **fabric-sdk-node**: [fabric-sdk-node](https://github.com/hyperledger/fabric-sdk-node)
    - [x] **fabric-sdk-go**: [fabric-sdk-go](https://github.com/hyperledger/fabric-sdk-go)
    - [x] **fabric-sdk-python**: [fabric-sdk-python](https://github.com/hyperledger/fabric-sdk-py)                      



### 1. resources 目录结构说明
```bash
    # 1. 链码存放目录
    chaincodes
        EG: /resources/chaincodes/sample/src/github.com/chaincode_example02/chaincode_example02.go
            chaincodePath: github.com/chaincode_example02
            chaincodeSourceLocation: .../chaincodes/sample  # ... 请补全绝对路径信息
    
    # 2. 通道配置信息、锚节点及创世纪块文件存放目录
    channel-artifacts
    
    # 3. 证书、签名密钥信息文件存放目录
    crypto-config
    
    # 4. 背书策略目录
    policy
    
    # 5. 协议文件目录(此版本暂无用到)
    protocol
```


### 2. Fabric V1.0 通道配置信息及证书手动生成
```bash
    # 加入脚本目录
    cd /opt/gopath/src/github.com/hyperledger/fabric/examples/e2e_cli
    
    # 备份已有配置信息
    mkdir bak
    cp -R channel-artifacts  crypto-config bak/
    
    # 移除已有配置信息, (如果现有配置信息还有用, 请先备份)
    rm -rf channel-artifacts/* crypto-config/*
    
    # 执行脚本, $channelName 为通道名称变量名
    source generateArtifacts.sh $channelName  
    
    # 验证
    ll channel-artifacts
    ll crypto-config                 
```


### 3. 关闭每次重启fabric服务重新生成通道及配置文件信息
```bash
    # 首次启动 network_setup.sh 脚本后, 修改以下代码, 重启 Fabric 服务
    vim network_setup.sh
    # 删除以下脚本代码
    #    function networkUp   -> source generateArtifacts.sh $CH_NAME
    #    function networkDown -> rm -rf channel-artifacts/*.block channel-artifacts/*.tx crypto-config
```


### 4. 注释掉tls配置
```bash
    cd /opt/gopath/src/github.com/hyperledger/fabric/examples/e2e_cli
    1. vim base/peer-base.yaml
        CORE_PEER_TLS_ENABLED=true 
            改为
        CORE_PEER_TLS_ENABLED=false


    2. vim base/docker-compose-base.yaml
        ORDERER_GENERAL_TLS_ENABLED=true
            改为
        ORDERER_GENERAL_TLS_ENABLED=false
    
    3. vim docker-compose-cli.yaml
        CORE_PEER_TLS_ENABLED=true
            改为
        CORE_PEER_TLS_ENABLED=false  
                  
    4. 如果使用 Fabric V1.0 E2E 测试转账DEMO, 请注释掉 script.sh 脚本所有操作区块链代码
    cd /opt/gopath/src/github.com/hyperledger/fabric/examples/e2e_cli
    vim scripts/script.sh        
        # 注释块起始位置(包含以下部分)  
            ## Create channel
            echo "Creating channel..."
            createChannel
        # 注释块截至位置(包含以下部分)
            #Query on chaincode on Peer3/Org2, check if the result is 90
            echo "Querying chaincode on org2/peer3..."
            chaincodeQuery 3 90                                                    
```


### 5. 附录 - 全流程测试用例 APITest 运行日志
```bash
    [DEBUG] 2018-09-05 14:38:28.651 com.hyperledger.fabric.sdk.handler.ApiHandler:[60] 构建Hyperledger Fabric客户端实例 Start...
    log4j:WARN No appenders could be found for logger (org.hyperledger.fabric.sdk.helper.Config).
    log4j:WARN Please initialize the log4j system properly.
    log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.
    [DEBUG] 2018-09-05 14:38:29.520 com.hyperledger.fabric.sdk.handler.ApiHandler:[87] 构建Hyperledger Fabric客户端实例 End!!!
    [DEBUG] 2018-09-05 14:38:29.523 com.hyperledger.fabric.sdk.handler.ApiHandler:[115] 创建通道 Start, channelName: mychannel.
    [DEBUG] 2018-09-05 14:38:30.620 com.hyperledger.fabric.sdk.handler.ApiHandler:[139] order节点: orderer.example.com 已成功加入通道.
    [DEBUG] 2018-09-05 14:38:30.791 com.hyperledger.fabric.sdk.handler.ApiHandler:[166] peer节点: peer0.org1.example.com 已成功加入通道.
    [DEBUG] 2018-09-05 14:38:30.793 com.hyperledger.fabric.sdk.handler.ApiHandler:[170] eventHub节点: peer0.org1.example.com 已成功加入通道.
    [DEBUG] 2018-09-05 14:38:30.893 com.hyperledger.fabric.sdk.handler.ApiHandler:[166] peer节点: peer1.org1.example.com 已成功加入通道.
    [DEBUG] 2018-09-05 14:38:30.893 com.hyperledger.fabric.sdk.handler.ApiHandler:[170] eventHub节点: peer1.org1.example.com 已成功加入通道.
    [DEBUG] 2018-09-05 14:38:31.173 com.hyperledger.fabric.sdk.handler.ApiHandler:[145] 创建通道 End, channelName: mychannel, isInitialized: true.
    [INFO] 2018-09-05 14:38:31.197 com.hyperledger.fabric.sdk.handler.ApiHandler:[150] 通道对象已放入redis缓存, key: hyperledger:fabric:cache:channel:mychannel.
    [DEBUG] 2018-09-05 14:38:31.199 com.hyperledger.fabric.sdk.handler.ApiHandler:[186] 安装智能合约 Start, chaincode name: mycc, chaincode path: github.com/chaincode_example02.
    [INFO] 2018-09-05 14:38:31.276 com.hyperledger.fabric.sdk.handler.ApiHandler:[370] response status: SUCCESS, isVerified: false from peer: peer0.org1.example.com, payload: 【nil】.
    [INFO] 2018-09-05 14:38:31.276 com.hyperledger.fabric.sdk.handler.ApiHandler:[370] response status: SUCCESS, isVerified: false from peer: peer1.org1.example.com, payload: 【nil】.
    [DEBUG] 2018-09-05 14:38:31.276 com.hyperledger.fabric.sdk.handler.ApiHandler:[203] 安装智能合约 End, chaincode name: mycc, chaincode path: github.com/chaincode_example02.
    [DEBUG] 2018-09-05 14:38:31.278 com.hyperledger.fabric.sdk.handler.ApiHandler:[253] 初始化智能合约 Start, channelName: mychannel, fcn: init, args: [a, 2300, b, 2400]
    [INFO] 2018-09-05 14:38:57.956 com.hyperledger.fabric.sdk.handler.ApiHandler:[370] response status: SUCCESS, isVerified: true from peer: peer0.org1.example.com, payload: 【nil】.
    [INFO] 2018-09-05 14:38:57.956 com.hyperledger.fabric.sdk.handler.ApiHandler:[370] response status: SUCCESS, isVerified: true from peer: peer1.org1.example.com, payload: 【nil】.
    [DEBUG] 2018-09-05 14:38:57.956 com.hyperledger.fabric.sdk.handler.ApiHandler:[341] 提交到orderer节点进行共识 Start...
    [DEBUG] 2018-09-05 14:39:00.066 com.hyperledger.fabric.sdk.handler.ApiHandler:[344] 提交到orderer共识 End, Type: TRANSACTION_ENVELOPE, TransactionActionInfoCount: 1, isValid: true, ValidationCode: 0.
    [DEBUG] 2018-09-05 14:39:00.066 com.hyperledger.fabric.sdk.handler.ApiHandler:[272] 初始化智能合约 End, channelName: mychannel, fcn: init, args: [a, 2300, b, 2400]
    [DEBUG] 2018-09-05 14:39:00.066 com.hyperledger.fabric.sdk.handler.ApiHandler:[284] 查询智能合约 Start, channelName: mychannel, fcn: query, args: [b]
    [INFO] 2018-09-05 14:39:00.132 com.hyperledger.fabric.sdk.handler.ApiHandler:[370] response status: SUCCESS, isVerified: true from peer: peer0.org1.example.com, payload: 2400.
    [INFO] 2018-09-05 14:39:00.132 com.hyperledger.fabric.sdk.handler.ApiHandler:[370] response status: SUCCESS, isVerified: true from peer: peer1.org1.example.com, payload: 2400.
    [DEBUG] 2018-09-05 14:39:00.132 com.hyperledger.fabric.sdk.handler.ApiHandler:[296] 查询智能合约 End, channelName: mychannel, fcn: query, args: [b]
    [DEBUG] 2018-09-05 14:39:00.132 com.hyperledger.fabric.sdk.handler.ApiHandler:[308] 交易智能合约 Start, channelName: mychannel, fcn: invoke, args: [a, b, 7]
    [INFO] 2018-09-05 14:39:00.197 com.hyperledger.fabric.sdk.handler.ApiHandler:[370] response status: SUCCESS, isVerified: true from peer: peer0.org1.example.com, payload: nil.
    [INFO] 2018-09-05 14:39:00.197 com.hyperledger.fabric.sdk.handler.ApiHandler:[370] response status: SUCCESS, isVerified: true from peer: peer1.org1.example.com, payload: nil.
    [DEBUG] 2018-09-05 14:39:00.197 com.hyperledger.fabric.sdk.handler.ApiHandler:[341] 提交到orderer节点进行共识 Start...
    [DEBUG] 2018-09-05 14:39:02.311 com.hyperledger.fabric.sdk.handler.ApiHandler:[344] 提交到orderer共识 End, Type: TRANSACTION_ENVELOPE, TransactionActionInfoCount: 1, isValid: true, ValidationCode: 0.
    [DEBUG] 2018-09-05 14:39:02.312 com.hyperledger.fabric.sdk.handler.ApiHandler:[320] 交易智能合约 End, channelName: mychannel, fcn: invoke, args: [a, b, 7]
    [DEBUG] 2018-09-05 14:39:02.312 com.hyperledger.fabric.sdk.handler.ApiHandler:[284] 查询智能合约 Start, channelName: mychannel, fcn: query, args: [b]
    [INFO] 2018-09-05 14:39:02.385 com.hyperledger.fabric.sdk.handler.ApiHandler:[370] response status: SUCCESS, isVerified: true from peer: peer0.org1.example.com, payload: 2407.
    [INFO] 2018-09-05 14:39:02.385 com.hyperledger.fabric.sdk.handler.ApiHandler:[370] response status: SUCCESS, isVerified: true from peer: peer1.org1.example.com, payload: 2407.
    [DEBUG] 2018-09-05 14:39:02.385 com.hyperledger.fabric.sdk.handler.ApiHandler:[296] 查询智能合约 End, channelName: mychannel, fcn: query, args: [b]
    [DEBUG] 2018-09-05 14:39:02.385 com.hyperledger.fabric.sdk.handler.ApiHandler:[284] 查询智能合约 Start, channelName: mychannel, fcn: query, args: [a]
    [INFO] 2018-09-05 14:39:02.440 com.hyperledger.fabric.sdk.handler.ApiHandler:[370] response status: SUCCESS, isVerified: true from peer: peer0.org1.example.com, payload: 2293.
    [INFO] 2018-09-05 14:39:02.440 com.hyperledger.fabric.sdk.handler.ApiHandler:[370] response status: SUCCESS, isVerified: true from peer: peer1.org1.example.com, payload: 2293.
    [DEBUG] 2018-09-05 14:39:02.440 com.hyperledger.fabric.sdk.handler.ApiHandler:[296] 查询智能合约 End, channelName: mychannel, fcn: query, args: [a]
```

***
> Created By **L.Answer At 2018-08-28 09:23:24** <br>
> GitHub: [https://github.com/AnswerCoder](https://github.com/AnswerCoder) <br>
> Contact me By Email **answer_ljm@163.com** or By QQ **1072594307** <br>
> **To You** 好用请**Star Star Star**, 不好用请弃置 <br>
> End.


