package com.hyperledger.fabric.sdk.handler;

import com.hyperledger.fabric.sdk.entity.dto.api.*;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by L.Answer on 2018-09-03 17:42
 *
 * SDK-API 测试用例
 *
 * 测试时需要把resources目录赋值到test目录下
 */
public class APITest {

    static String cxtPath = APITest.class.getClassLoader().getResource("").getPath();
    static String channelName = "mychannel";

    public static void main(String[] args) throws Exception {
        String chaincodeVersion = "1.0";
        String chaincodePath = "github.com/chaincode_example02";
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName("mycc").setVersion(chaincodeVersion).setPath(chaincodePath).build();

        String mspPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();

        HFClient client = ApiHandler.clientBuild(buildClientDTO);

        OrderNodeDTO orderNodeDTO = new OrderNodeDTO("orderer.example.com", "grpc://119.23.106.146:7050");
        Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        peerNodeDTOS.add(new PeerNodeDTO("peer0.org1.example.com", "grpc://119.23.106.146:7051", "grpc://119.23.106.146:7053"));
        peerNodeDTOS.add(new PeerNodeDTO("peer1.org1.example.com", "grpc://119.23.106.146:8051", "grpc://119.23.106.146:8053"));

        CreateChannelDTO createChannelDTO = new CreateChannelDTO();
        createChannelDTO.setChannelConfigPath(cxtPath + "channel-artifacts/channel.tx");
        createChannelDTO.setOrderNodeDTO(orderNodeDTO);
        createChannelDTO.setPeerNodeDTOS(peerNodeDTOS);
        Channel channel = ApiHandler.createChannel(client, channelName, createChannelDTO);

        ExecuteCCDTO querybCCDTO = new ExecuteCCDTO.Builder().funcName("query").params(new String[] {"b"}).chaincodeID(chaincodeID).build();
        ApiHandler.queryChainCode(client, channel, querybCCDTO);

        ExecuteCCDTO invokeCCDTO = new ExecuteCCDTO.Builder().funcName("invoke").params(new String[] {"a", "b", "7"}).chaincodeID(chaincodeID).build();
        ApiHandler.invokeChainCode(client, channel, invokeCCDTO);

        ApiHandler.queryChainCode(client, channel, querybCCDTO);

        ExecuteCCDTO queryaCCDTO = new ExecuteCCDTO.Builder().funcName("query").params(new String[] {"a"}).chaincodeID(chaincodeID).build();
        ApiHandler.invokeChainCode(client, channel, queryaCCDTO);

    }

}