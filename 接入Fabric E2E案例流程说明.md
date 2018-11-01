# 接入Fabric E2E案例流程说明 - 前提: Fabric 1.0网络已经搭建成功
  - [x] **fabric项目服务器路径**: /opt/gopath/src/github.com/hyperledger/fabric       **`视个人项目路径而定`**
  - [x] **说明**: 以下所有步骤没特别说明, 均在 **/opt/gopath/src/github.com/hyperledger/fabric/examples/e2e_cli/** 目录下操作

### 1.  关闭tls配置
```bash
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
```bash
    # 注意是替换到 src/test/resources 目录下, 而不是 src/resources 目录
```


### 4. 可选-每次重启Farbci网络都会重新生成通道配置信息和证书, 如果不进行此步骤操作, 每次重启Fabric网络时都【需要重新操作步骤3】
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



### 附录. 报错说明
```bash
    # SDK启动报错信息
:<<annotation
    Exception in thread "main" org.hyperledger.fabric.sdk.exception.TransactionException: org.hyperledger.fabric.sdk.exception.EventHubException: UNAVAILABLE
    	at org.hyperledger.fabric.sdk.Channel.initialize(Channel.java:742)
    	at com.hyperledger.fabric.sdk.handler.ApiHandler.joinPeers(ApiHandler.java:238)
    	at com.hyperledger.fabric.sdk.handler.ApiHandler.createChannel(ApiHandler.java:147)
    	at com.hyperledger.fabric.sdk.handler.APITest.main(APITest.java:63)
    Caused by: org.hyperledger.fabric.sdk.exception.EventHubException: UNAVAILABLE
    	at org.hyperledger.fabric.sdk.EventHub.connect(EventHub.java:301)
    	at org.hyperledger.fabric.sdk.Channel.initialize(Channel.java:724)
    	... 3 more
    Caused by: io.grpc.StatusRuntimeException: UNAVAILABLE
    	at io.grpc.Status.asRuntimeException(Status.java:526)
    	at io.grpc.stub.ClientCalls$StreamObserverToCallListenerAdapter.onClose(ClientCalls.java:385)
    	at io.grpc.ForwardingClientCallListener.onClose(ForwardingClientCallListener.java:41)
    	at io.grpc.internal.CensusTracingModule$TracingClientInterceptor$1$1.onClose(CensusTracingModule.java:339)
    	at io.grpc.internal.ClientCallImpl.closeObserver(ClientCallImpl.java:443)
    	at io.grpc.internal.ClientCallImpl.access$300(ClientCallImpl.java:63)
    	at io.grpc.internal.ClientCallImpl$ClientStreamListenerImpl.close(ClientCallImpl.java:525)
    	at io.grpc.internal.ClientCallImpl$ClientStreamListenerImpl.access$600(ClientCallImpl.java:446)
    	at io.grpc.internal.ClientCallImpl$ClientStreamListenerImpl$1StreamClosed.runInContext(ClientCallImpl.java:557)
    	at io.grpc.internal.ContextRunnable.run(ContextRunnable.java:37)
    	at io.grpc.internal.SerializingExecutor.run(SerializingExecutor.java:107)
    	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
    	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
    	at java.lang.Thread.run(Thread.java:748)
    Caused by: io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: no further information: /119.23.106.146:7053
    	at sun.nio.ch.SocketChannelImpl.checkConnect(Native Method)
    	at sun.nio.ch.SocketChannelImpl.finishConnect(SocketChannelImpl.java:717)
    	at io.netty.channel.socket.nio.NioSocketChannel.doFinishConnect(NioSocketChannel.java:323)
    	at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe.finishConnect(AbstractNioChannel.java:340)
    	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:633)
    	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:580)
    	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:497)
    	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:459)
    	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:858)
    	at io.netty.util.concurrent.DefaultThreadFactory$DefaultRunnableDecorator.run(DefaultThreadFactory.java:138)
    	... 1 more
    Caused by: java.net.ConnectException: Connection refused: no further information
    	... 11 more
    
    Process finished with exit code 1
annotation
    
    # 解决方案: 拉取的镜像修改为对应的版本即可
        vim base/peer-base.yaml
            image: hyperledger/fabric-peer      =>  image: hyperledger/fabric-peer:x86_64-1.0.5
        
        vim base/docker-compose-base.yaml
            image: hyperledger/fabric-orderer   =>  image: hyperledger/fabric-orderer:x86_64-1.0.5
            
        vim docker-compose-cli.yaml
            image: hyperledger/fabric-tools     =>  image: hyperledger/fabric-tools:x86_64-1.0.5                    
             
    
```