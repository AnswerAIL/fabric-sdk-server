package com.hyperledger.fabric.sdk.handler;

import com.hyperledger.fabric.sdk.entity.dto.api.BuildClientDTO;
import com.hyperledger.fabric.sdk.entity.dto.api.ExecuteCCDTO;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;

/**
 * Created by L.Answer on 2018-09-04 16:45
 *
 * 查询智能合约测试用例
 *
 * 注意修改 chaincodeVersion 的版本号
 */
public class QueryTest {

    public static void main(String[] args) throws Exception {
        /* 通道名称 */
        String channelName = "mychannel";

         /* 智能合约配置信息 */
        String chaincodeName = "mycc";
        String chaincodeVersion = "1.2";
        String chaincodePath = "github.com/chaincode_example02";
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();


        // 1. 初始化客户端
        String mspPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = ApiHandler.clientBuild(buildClientDTO);



        // 2. 创建通道
        Channel channel = ApiHandler.createChannel(client, channelName, null);


        // 3. 查询, 账户a的余额
        ExecuteCCDTO querybCCDTO = new ExecuteCCDTO.Builder().funcName("query").params(new String[] {"a"}).chaincodeID(chaincodeID).build();
        ApiHandler.queryChainCode(client, channel, querybCCDTO);

    }

}