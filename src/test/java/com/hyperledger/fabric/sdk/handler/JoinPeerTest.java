package com.hyperledger.fabric.sdk.handler;

import com.hyperledger.fabric.sdk.entity.dto.api.*;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.HFClient;

import java.util.ArrayList;
import java.util.Collection;

import static com.hyperledger.fabric.sdk.common.Config.*;

/**
 * Created by L.Answer on 2018-09-05 08:59
 *
 * 加入新节点测试用例
 *
 * 此测试用例将组织 Org2MSP 下的节点加入通道 mychannel
 *
 *
 * 单个节点逐一加入通道步骤说明:
 *  1: 注释掉 PEER0_ORG2_NAME 节点代码
 *      peerNodeDTOS.add(new PeerNodeDTO(PEER0_ORG2_NAME, PEER0_ORG2_GRPC_URL, PEER0_ORG2_EVENT_URL));
 *      // peerNodeDTOS.add(new PeerNodeDTO(PEER1_ORG2_NAME, PEER1_ORG2_GRPC_URL, PEER1_ORG2_EVENT_URL));
 *
 *  2: 运行程序
 *
 *  3: 注释掉 PEER0_ORG2_NAME 节点代码
 *     // peerNodeDTOS.add(new PeerNodeDTO(PEER0_ORG2_NAME, PEER0_ORG2_GRPC_URL, PEER0_ORG2_EVENT_URL));
 *       peerNodeDTOS.add(new PeerNodeDTO(PEER1_ORG2_NAME, PEER1_ORG2_GRPC_URL, PEER1_ORG2_EVENT_URL));
 *
 *  4: ApiHandler.joinPeers(client, channelName, orderNodeDTOS, peerNodeDTOS, false);
 *           改为
 *       ApiHandler.joinPeers(client, channelName, orderNodeDTOS, peerNodeDTOS);
 *
 *  5: 运行程序
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
        orderNodeDTOS.add(new OrderNodeDTO(ORDER_NAME, ORDER_GRPC_URL));

        Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        peerNodeDTOS.add(new PeerNodeDTO(PEER0_ORG2_NAME, PEER0_ORG2_GRPC_URL, PEER0_ORG2_EVENT_URL));
        peerNodeDTOS.add(new PeerNodeDTO(PEER1_ORG2_NAME, PEER1_ORG2_GRPC_URL, PEER1_ORG2_EVENT_URL));
        ApiHandler.joinPeers(client, channelName, orderNodeDTOS, peerNodeDTOS, false);



        // 3. 安装智能合约
        InstallCCDTO installCCDTO = new InstallCCDTO.Builder().chaincodeID(chaincodeID).chaincodeSourceLocation(cxtPath + "chaincodes/sample").peerNodeDTOS(peerNodeDTOS).build();
        ApiHandler.installChainCode(client, installCCDTO);

    }

}