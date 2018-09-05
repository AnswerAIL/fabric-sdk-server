package com.hyperledger.fabric.sdk.handler;

import com.hyperledger.fabric.sdk.entity.dto.api.BuildClientDTO;
import com.hyperledger.fabric.sdk.entity.dto.api.ExecuteCCDTO;
import com.hyperledger.fabric.sdk.entity.dto.api.InstallCCDTO;
import com.hyperledger.fabric.sdk.entity.dto.api.PeerNodeDTO;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by L.Answer on 2018-09-04 17:51
 *
 * 升级智能合约测试用例
 */
public class UpgradeTest {

    public static void main(String[] args) throws Exception {
        String cxtPath = APITest.class.getClassLoader().getResource("").getPath();

        /* 通道名称 */
        String channelName = "mychannel";

         /* 智能合约配置信息 */
        String chaincodeName = "mycc";
        String chaincodeVersion = "1.2";    // 升级合约版本号
        String chaincodePath = "github.com/chaincode_example02";
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();


        // 1. 初始化客户端
        String mspPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = ApiHandler.clientBuild(buildClientDTO);



        // 2. 创建通道
        Channel channel = ApiHandler.createChannel(client, channelName, null);

        // 3. 安装新版本智能合约
        Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        peerNodeDTOS.add(new PeerNodeDTO("peer0.org1.example.com", "grpc://119.23.XXX.XXX:7051", "grpc://119.23.XXX.XXX:7053"));
        peerNodeDTOS.add(new PeerNodeDTO("peer1.org1.example.com", "grpc://119.23.XXX.XXX:8051", "grpc://119.23.XXX.XXX:8053"));
        InstallCCDTO installCCDTO = new InstallCCDTO.Builder().chaincodeID(chaincodeID).chaincodeSourceLocation(cxtPath + "chaincodes/sample").peerNodeDTOS(peerNodeDTOS).build();
        ApiHandler.installChainCode(client, installCCDTO);

        // 4. 升级智能合约
        ExecuteCCDTO upgradeCCDTO = new ExecuteCCDTO.Builder().funcName("init").params(new String[] {"a", "12300", "b", "12400"}).chaincodeID(chaincodeID).build();
        ApiHandler.upgradeChainCode(client, channel, upgradeCCDTO);

    }


}