# Fabric-SDK-Java 封装版项目
  - [x] **适用对象**: 入门 Hyperledger Fabric SDK
  - [x] **缓存说明**: 如需使用缓存请先安装Redis, 配置服务信息请参见: Constants 类
  - [x] **测试用例**: APITest | BlockChainTest | InvokeTest | QueryTest | UpgradeTest
    - [x] **APITest**: 全流程测试案例
    - [x] **BlockChainTest**: 查询块信息测试案例
    - [x] **InvokeTest**: 转账测试案例
    - [x] **QueryTest**: 查询测试案例
    - [x] **UpgradeTest**: 升级智能合约测试案例
  - [x] **Fabric版本**： release-1.0  [Fabric Link](https://github.com/hyperledger/fabric)



### resources 目录结构说明
```bash
    # 1. 链码存放目录
    chaincodes
        EG: /resources/chaincodes/sample/src/github.com/chaincode_example02/chaincode_example02.go
            chaincodePath: github.com/chaincode_example02
            chaincodeSourceLocation: .../chaincodes/sample
    
    # 2. 通道配置信息目录
    channel-artifacts
    
    # 3. 证书信息目录
    crypto-config
    
    # 4. 背书策略目录
    policy
    
    # 5. 协议文件目录(此版本暂无用到)
    protocol
```


### Fabric V1.0 通道配置信息及证书手动生成
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


### 关闭每次重启fabric服务重新生成通道及配置文件信息
```bash
    # 首次启动 network_setup.sh 脚本后, 修改以下代码, 重启 Fabric 服务
    vim network_setup.sh
    # 删除以下脚本代码
    #    function networkUp   -> source generateArtifacts.sh $CH_NAME
    #    function networkDown -> rm -rf channel-artifacts/*.block channel-artifacts/*.tx crypto-config
```


### 注意事项
```bash
    # 注释掉tls配置
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
```


### 附录 - APITest 测试用例运行日志
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
`Created By L.Answer At 2018-08-28 09:23:24`
***


