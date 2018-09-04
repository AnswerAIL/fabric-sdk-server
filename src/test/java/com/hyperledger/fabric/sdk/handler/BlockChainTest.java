package com.hyperledger.fabric.sdk.handler;

import com.hyperledger.fabric.sdk.entity.dto.api.BuildClientDTO;
import org.hyperledger.fabric.sdk.*;


/**
 * Created by L.Answer on 2018-09-04 14:08
 *
 * 说明: 本测试案例中channel对象已经存储在redis缓存中
 */
public class BlockChainTest {

    public static void main(String[] args) throws Exception {
        /* 通道名称 */
        String channelName = "mychannel";


        // 1. 初始化客户端
        String mspPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = ApiHandler.clientBuild(buildClientDTO);



        // 2. 创建通道
        Channel channel = ApiHandler.createChannel(client, channelName, null);


        QueryHandler.queryBlockChain(channel);
    }



}