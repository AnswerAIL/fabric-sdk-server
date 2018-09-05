package com.hyperledger.fabric.sdk.handler;

import com.hyperledger.fabric.sdk.entity.dto.api.*;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by L.Answer on 2018-09-05 08:59
 *
 * 加入新节点测试用例
 *
 * 此测试用例将组织 Org2MSP 下的节点加入通道 mychannel
 */
public class JoinPeerTest {

    public static void main(String[] args) throws Exception {
        String cxtPath = APITest.class.getClassLoader().getResource("").getPath();

        /* 通道名称 */
        String channelName = "mychannel";

         /* 智能合约配置信息 */
        String chaincodeName = "mycc";
        String chaincodeVersion = "1.0";
        String chaincodePath = "github.com/chaincode_example02";
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();


        // 1. 初始化客户端
        String mspPath = "crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org2.example.com").mspId("Org2MSP").mspPath(mspPath).build();
        HFClient client = ApiHandler.clientBuild(buildClientDTO);



        // 2. 加入新节点
        Collection<OrderNodeDTO> orderNodeDTOS = new ArrayList<>();
        orderNodeDTOS.add(new OrderNodeDTO("orderer.example.com", "grpc://119.23.106.146:7050"));

        Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        peerNodeDTOS.add(new PeerNodeDTO("peer0.org2.example.com", "grpc://119.23.106.146:9051", "grpc://119.23.106.146:9053"));
        peerNodeDTOS.add(new PeerNodeDTO("peer1.org2.example.com", "grpc://119.23.106.146:10051", "grpc://119.23.106.146:10053"));
        ApiHandler.joinPeers(client, channelName, orderNodeDTOS, peerNodeDTOS);



        // 3. 安装智能合约
        InstallCCDTO installCCDTO = new InstallCCDTO.Builder().chaincodeID(chaincodeID).chaincodeSourceLocation(cxtPath + "chaincodes/sample").peerNodeDTOS(peerNodeDTOS).build();
        ApiHandler.installChainCode(client, installCCDTO);

    }

}