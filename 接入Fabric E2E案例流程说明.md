# 接入Fabric E2E案例流程说明 - 前提: Fabric 1.0网络已经搭建成功
  - [x] **fabric项目服务器路径**: /opt/gopath/src/github.com/hyperledger/fabric       **`视个人项目路径而定`**
  - [x] **说明**: 以下所有步骤没特别说明, 均在 **/opt/gopath/src/github.com/hyperledger/fabric/** 目录下操作

### 1.  关闭tls配置
```bash
    # 进入fabric e2e_cli目录
    cd examples/e2e_cli/
    
    # 1. 
    vim base/peer-base.yaml
        CORE_PEER_TLS_ENABLED=true          # 在18行左右
            改为
        CORE_PEER_TLS_ENABLED=false

    vim base/docker-compose-base.yaml
        ORDERER_GENERAL_TLS_ENABLED=true    # 在21行左右
            改为
        ORDERER_GENERAL_TLS_ENABLED=false
    
    vim docker-compose-cli.yaml
        CORE_PEER_TLS_ENABLED=true          # 在51行左右
            改为
        CORE_PEER_TLS_ENABLED=false
```


### 2. 关闭 script.sh 脚本中操作区块链网络代码
```bash
    # 编辑 script.sh 脚本文件
    vim scripts/script.sh
    
    # 注释掉  189-227 行的代码, 如下所示
    
        :>>annotate
        ## Create channel                                       <------------------ 注释起始位置
        echo "Creating channel..."
        createChannel
        
        ## Join all the peers to the channel
        echo "Having all peers join the channel..."
        joinChannel
        
        ## Set the anchor peers for each org in the channel
        echo "Updating anchor peers for org1..."
        updateAnchorPeers 0
        echo "Updating anchor peers for org2..."
        updateAnchorPeers 2
        
        ## Install chaincode on Peer0/Org1 and Peer2/Org2
        echo "Installing chaincode on org1/peer0..."
        installChaincode 0
        echo "Install chaincode on org2/peer2..."
        installChaincode 2
        
        #Instantiate chaincode on Peer2/Org2
        echo "Instantiating chaincode on org2/peer2..."
        instantiateChaincode 2
        
        #Query on chaincode on Peer0/Org1
        echo "Querying chaincode on org1/peer0..."
        chaincodeQuery 0 100 
        
        #Invoke on chaincode on Peer0/Org1
        echo "Sending invoke transaction on org1/peer0..."
        chaincodeInvoke 0
        
        ## Install chaincode on Peer3/Org2
        echo "Installing chaincode on org2/peer3..."
        installChaincode 3
        
        #Query on chaincode on Peer3/Org2, check if the result is 90
        echo "Querying chaincode on org2/peer3..."
        chaincodeQuery 3 90                                 <------------------ 注释截至位置
        annotate
```


### 3. 下载 channel-artifacts和crypto-config 目录到本地, 并将项目 src/test/resources 目录下的 channel-artifacts和crypto-config 替换掉


### 4. 每次重启Farbci网络都会重新生成通道配置信息和证书, 如果不进行此步骤操作, 每次重启Fabric网络时都【重新需要操作步骤3】
```bash
    # 编辑启动脚本 network_setup.sh
    vim network_setup.sh
    
    # 删除 57-60 行代码
        #   else
        #      #Generate all the artifacts that includes org certs, orderer genesis block,
        #      # channel configuration transaction
        #      source generateArtifacts.sh $CH_NAME
        
    # 删除 84-85 行代码
        #    # remove orderer block and other channel configuration transactions and certs
        #    rm -rf channel-artifacts/*.block channel-artifacts/*.tx crypto-config
    
```

### 5. 重启Fabric网络, 使用answer-fabric-sdk来操作区块链网络
```bash
    ./network_setup.sh restart
```