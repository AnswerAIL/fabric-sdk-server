# Fabric-SDK-Java 封装版项目 fabric-sdk-server

使用前提： 区块链网络环境已经搭建且只能合约安装和初始化完毕

&nbsp;

使用 Network-Config 配置来使用已创建通道并操作区块链网络
 
 - 修改 network-config.yaml 区块链环境配置文件信息 
 
 - 替换 `src/test/resources/msp` 目录下的用户证书文件
   - e.g. org1组织的用户证书对应目录： `crypto-config/peerOrganizations/org1.aiqkl.com/users/` 下的任何一个用户证书

> 注意： 当前测试所用的 通道名称为： mychannel， 智能合约名称为： mycc

### 区块链网络目录结构
```bash
├── data
└── docker-compose.yaml
```


**data目录结构**
```bash
data/
├── orderers
│   └── orderer1        # 对应目录： crypto-config/ordererOrganizations/aiqkl.com/orderers/orderer1.aiqkl.com
│       ├── msp
│       ├── orderer.block
│       └── tls
└── peers
    └── org1
        ├── answer      # 对应目录： crypto-config/peerOrganizations/org1.aiqkl.com/peers/peer1.org1.aiqkl.com
        ├── jaemon      # 对应目录： crypto-config/peerOrganizations/org1.aiqkl.com/peers/peer2.org1.aiqkl.com
        └── root        # 对应目录： crypto-config/peerOrganizations/org1.aiqkl.com/peers/peer0.org1.aiqkl.com

```