# Hyperledger Fabric 单机环境部署 - Ubuntu
***
### 1. 安装 Docker
```bash
    # 安装必要的一些系统工具
    sudo apt-get update
    
    sudo apt-get -y install apt-transport-https ca-certificates curl software-properties-common
    
    # 安装GPG证书
    curl -fsSL http://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | sudo apt-key add -
    
    # 写入软件源信息
    sudo add-apt-repository "deb [arch=amd64] http://mirrors.aliyun.com/docker-ce/linux/ubuntu $(lsb_release -cs) stable"
    
    # 更新并安装 Docker-CE
    sudo apt-get -y update
    sudo apt-get -y install docker-ce
    docker --version
```



### 2. 安装 Docker-compose

```bash
    # 安装好pip
    sudo apt-get install python-pip -y
    
    # 用pip安装docker-compose
    pip install docker-compose
    
    docker-compose version 
    sudo docker version
```



### 3. 安装 Git 并拉取 Fabric 代码

```bash
    # 安装git
    sudo apt-get install git
    
    # 创建 hyperledger 目录
    mkdir -p /opt/gopath/src/github.com/hyperledger
    
    # 进入到 hyperledger 目录
    cd hyperledger
    
    # 拉取fabric代码
    git clone https://github.com/hyperledger/fabric.git
    
    # 切换到1.1分支上
    git checkout -b release-1.1 origin/release-1.1
    
    # 拉取 Docker 镜像(时间较长)及一些可执行文件
    cd /opt/gopath/src/github.com/hyperledger/fabric/scripts
    # root用户下执行
    ./bootstrap.sh 	 	
    # 非root用户下执行
    sudo ./bootstrap.sh	
```

`特别说明: 超级账本源码下载目录: /opt/gopath/src/github.com/hyperledger/fabric`



### 4. 安装 GO 语言

```bash
    # 安装 GO 语言
    sudo apt-get install golang -y
    
    # 查看 go 版本信息
    go version
    
    # 查看go的环境变量
    go env
    
    # 查看go的安装目录
    go env | grep "GOROOT"
    
    # 配置环境变量
    sudo vim /etc/profile
    # 加入以下内容: 
    export PATH=$PATH:/usr/lib/go-1.6/bin
    export GOPATH=/opt/gopath
```



### 5. 修改一个阻塞执行的bug

```bash
    # 修改一个阻塞执行的bug
    cd /opt/gopath/src/github.com/hyperledger/fabric/examples/e2e_cli/base
    vim peer-base.yaml
    #	将
    	- CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE=e2ecli_default
    # 	修改为
    	- CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE=e2e_cli_default
```



### 6. 启动服务

```bash
    cd /data/hyperledger/fabric/examples/e2e_cli
    bash network_setup.sh up
```



### 7. 验证, 体验 fabric 系统功能

```bash
    # 查看 docker 实例
    sudo docker ps --format "{{.ID}}:{{.Names}}"
    
    # 进入 cli 执行 peer 命令
    sudo docker start cli
    # 进入 cli 实例
    sudo docker exec -it cli bash
    
    # 安装智能合约, 其中: -n表示合约名字, -p指向合约文件目录路径, -v是版本号
    peer chaincode install -n mycc -v 1.0 -p github.com/hyperledger/fabric/examples/chaincode/go/chaincode_example02
    
    # 初始化智能合约, 其中: -C指向channel名字, -c则是初始构造json格式的消息, -P是背书策略, -o指定共识节点. 这里置帐户a初始余额为100, 帐户b初始余额为200
    peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n mycc -v 1.0 -c '{"Args":["init","a","100","b","200"]}' -P "OR    ('Org1MSP.peer','Org2MSP.peer')"
    
    # 查询余额
    peer chaincode query -C mychannel -n mycc -c '{"Args":["query","a"]}'
    peer chaincode query -C mychannel -n mycc -c '{"Args":["query","b"]}'
    
    # 转账, 由 b 向 a 转70
    peer chaincode invoke -o orderer.example.com:7050  --tls true --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem  -C mychannel -n mycc -c '{"Args":["invoke","b","a","70"]}'
    
    # 再次查询 a 和 b 账户的余额
    peer chaincode query -C mychannel -n mycc -c '{"Args":["query","a"]}'
    peer chaincode query -C mychannel -n mycc -c '{"Args":["query","b"]}'
```



### 8. 附录A - Docker 加速配置

```bash
    sudo mkdir -p /etc/docker
    
    vim /etc/docker/daemon.json 
    # 添加以下内容到文件中
    {
      "registry-mirrors": ["https://c5xdtexs.mirror.aliyuncs.com"]
    }
    
    # Docker 重启
    sudo systemctl daemon-reload
    sudo systemctl restart docker
```



### 9. 附录B - Docker 镜像和容器相关路径修改

```bash
    # Docker 镜像和容器相关路径修改, 默认路径: /var/lib/docker/
    sudo vim /lib/systemd/system/docker.service
    # ExecStart 后面加上 --graph=/data/docker
    # EG:   将   ExecStart=/usr/bin/dockerd -H fd://
    #       改为 ExecStart=/usr/bin/dockerd -H fd:// --graph=/data/docker
```



### 10. 附录C - 执行 ./network_setup.sh up 报错

```bash
    # 1. ERROR: Couldn't connect to Docker daemon at http+docker://localhost - is it running?
        # 1-1: 查看配置文件
            sudo systemctl show docker | grep FragmentPath=
        # 1-2: 编辑 FragmentPath 映射的文件
            vim /lib/systemd/system/docker.service
                ExecStart=/usr/bin/dockerd -H unix:///var/run/docker.sock -H tcp://127.0.0.1:4243
        # 1-3: 配置/etc/default/docker
            vim /etc/default/docker
                DOCKER_OPTS="-H tcp://127.0.0.1:4243 -H unix:///var/run/docker.sock"
        # 1.4: 配置环境变量 DOCKER_HOST
            sudo vim /etc/profile
                export DOCKER_HOST=tcp://localhost:4243
            source /etc/profile               
        # 1.5: 重启 docker
            sudo systemctl daemon-reload
            sudo systemctl restart docker                 
    
    # 解决方案参考: https://segmentfault.com/a/1190000012570843
    
    # 2. ERROR: ERROR: for orderer.example.com  Cannot start service orderer.example.com: OCI runtime create failed: container_linux.go:348: starting container process caused "process_linux.go:402: container init caused \"rootfs_linux.go:58: mounting \\\"/opt/gopath/src/github.com/hyperledger/fabric/examples/e2e_cli/channel-artifacts/genesis.block\\\" to rootfs \\\"/var/lib/docker/overlay2/0748668b767cc21abe321cec7fe719db9fe625ed98fe1530e6cc546c5e7474ba/merged\\\" at \\\"/var/lib/docker/overlay2/0748668b767cc21abe321cec7fe719db9fe625ed98fe1530e6cc546c5e7474ba/merged/var/hyperledger/orderer/orderer.genesis.block\\\" caused \\\"not a directory\\\"\"": unknown: Are you trying to mount a directory onto a file (or vice-versa)? Check if the specified host path exists and is the expected type
    # 移除并备份 /var/lib/docker/ 下的文件
    sudo mv /var/lib/docker/ answer/
    # 停止 docker 服务
    sudo service docker stop
    # 启动 docker 服务
    sudo service docker start
    # 启动服务
    ./network_setup.sh up
    
    # 3. 其他报错临时解决方案
    # 关闭服务
    ./network_setup.sh down
    # 重新开启服务
    ./network_setup.sh up
```



### 11. Refer
  - [x] [安装docker](https://yq.aliyun.com/articles/110806)
  - [x] [Fabric快速部署及CLI体验](http://www.taohui.pub/530.html)
