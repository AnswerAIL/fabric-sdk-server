package com.hyperledger.fabric.sdk.handler;

import com.hyperledger.fabric.sdk.entity.dto.api.*;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by L.Answer on 2018-09-03 17:42
 *
 * SDK-API 测试用例
 *
 * 测试时需要把resources目录赋值到test目录下
 */
public class APITest {

    public static void main(String[] args) throws Exception {
        String channelName = "mychannel";

        String cxtPath = APITest.class.getClassLoader().getResource("").getPath();

        String chaincodeVersion = "1.0";
        String chaincodePath = "github.com/chaincode_example02";
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName("mycc").setVersion(chaincodeVersion).setPath(chaincodePath).build();



        // 1. 初始化客户端
        String mspPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = ApiHandler.clientBuild(buildClientDTO);



        // 2. 创建通道
        OrderNodeDTO orderNodeDTO = new OrderNodeDTO("orderer.example.com", "grpc://119.23.106.146:7050");
        Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        peerNodeDTOS.add(new PeerNodeDTO("peer0.org1.example.com", "grpc://119.23.106.146:7051", "grpc://119.23.106.146:7053"));
        peerNodeDTOS.add(new PeerNodeDTO("peer1.org1.example.com", "grpc://119.23.106.146:8051", "grpc://119.23.106.146:8053"));

        CreateChannelDTO createChannelDTO = new CreateChannelDTO();
        createChannelDTO.setChannelConfigPath(cxtPath + "channel-artifacts/channel.tx");
        createChannelDTO.setOrderNodeDTO(orderNodeDTO);
        createChannelDTO.setPeerNodeDTOS(peerNodeDTOS);
        Channel channel = ApiHandler.createChannel(client, channelName, createChannelDTO, false);



        // 3. 安装智能合约
        InstallCCDTO installCCDTO = new InstallCCDTO.Builder().chaincodeID(chaincodeID).chaincodeSourceLocation(cxtPath + "chaincodes/sample").peerNodeDTOS(peerNodeDTOS).build();
        ApiHandler.installChainCode(client, installCCDTO);



        // 4. 初始化智能合约
        ExecuteCCDTO initCCDTO = new ExecuteCCDTO.Builder().funcName("init").params(new String[] {"a", "2300", "b", "" + 2400}).chaincodeID(chaincodeID).build();
        ApiHandler.initializeChainCode(client, channel, initCCDTO);



        // 5. 查询智能合约
        ExecuteCCDTO querybCCDTO = new ExecuteCCDTO.Builder().funcName("query").params(new String[] {"b"}).chaincodeID(chaincodeID).build();
        ApiHandler.queryChainCode(client, channel, querybCCDTO);



        // 6. 转账
        ExecuteCCDTO invokeCCDTO = new ExecuteCCDTO.Builder().funcName("invoke").params(new String[] {"a", "b", "7"}).chaincodeID(chaincodeID).build();
        ApiHandler.invokeChainCode(client, channel, invokeCCDTO);



        // 7. 查询智能合约
        ApiHandler.queryChainCode(client, channel, querybCCDTO);



        // 8. 查询智能合约
        ExecuteCCDTO queryaCCDTO = new ExecuteCCDTO.Builder().funcName("query").params(new String[] {"a"}).chaincodeID(chaincodeID).build();
        ApiHandler.queryChainCode(client, channel, queryaCCDTO);

    }

}