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
    vim network_setup.sh
    # 删除以下脚本代码
    #    function networkUp   -> source generateArtifacts.sh $CH_NAME
    #    function networkDown -> rm -rf channel-artifacts/*.block channel-artifacts/*.tx crypto-config
```


### 注意事项
```bash
    # 注释掉tls配置
    1. vim base/peer-base.yaml
        CORE_PEER_TLS_ENABLED=true 
            改为
        CORE_PEER_TLS_ENABLED=false


    2. vim base/docker-compose-base.yaml
        ORDERER_GENERAL_TLS_ENABLED=true
            改为
        ORDERER_GENERAL_TLS_ENABLED=false
    
    3. vim examples/e2e_cli/docker-compose-cli.yaml
        CORE_PEER_TLS_ENABLED=true
            改为
        CORE_PEER_TLS_ENABLED=false            
```

***
`Created By L.Answer At 2018-08-28 09:23:24`
***

