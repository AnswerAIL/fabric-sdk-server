# Fabric-SDK Java封装版项目



### resources 目录结构说明
```bash
    # 链码存放目录
    chaincodes
    
    # 通道配置及证书信息
    channel-artifacts
    crypto-config
```









### 注意事项
```bash
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



