package com.hyperledger.fabric.sdk.handler;

import static com.hyperledger.fabric.sdk.common.Config.*;
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
 * 测试时需要把resources目录复制到test目录下
 *
 * 若报错: 初始化智能合约失败 解决方案
 *  1: Channel channel = ApiHandler.createChannel(client, channelName, createChannelDTO, false);
 *          改为
 *     Channel channel = ApiHandler.createChannel(client, channelName, createChannelDTO);
 *  2: 注释掉【3. 安装智能合约】代码
 *  3: 重新运行测试用例
 */
public class APITest {

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
        String mspPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = ApiHandler.clientBuild(buildClientDTO);



        // 2. 创建通道
        Collection<OrderNodeDTO> orderNodeDTOS = new ArrayList<>();
        orderNodeDTOS.add(new OrderNodeDTO(ORDER_NAME, ORDER_GRPC_URL));

        Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        peerNodeDTOS.add(new PeerNodeDTO(PEER0_ORG1_NAME, PEER0_ORG1_GRPC_URL, PEER0_ORG1_EVENT_URL));
        peerNodeDTOS.add(new PeerNodeDTO(PEER1_ORG1_NAME, PEER1_ORG1_GRPC_URL, PEER1_ORG1_EVENT_URL));

        CreateChannelDTO createChannelDTO = new CreateChannelDTO();
        createChannelDTO.setChannelConfigPath(cxtPath + "channel-artifacts/channel.tx");
        createChannelDTO.setOrderNodeDTOS(orderNodeDTOS);
        createChannelDTO.setPeerNodeDTOS(peerNodeDTOS);
        Channel channel = ApiHandler.createChannel(client, channelName, createChannelDTO, false);



        // 3. 安装智能合约
        InstallCCDTO installCCDTO = new InstallCCDTO.Builder().chaincodeID(chaincodeID).chaincodeSourceLocation(cxtPath + "chaincodes/sample").peerNodeDTOS(peerNodeDTOS).build();
        ApiHandler.installChainCode(client, installCCDTO);



        // 4. 初始化智能合约
        ExecuteCCDTO initCCDTO = new ExecuteCCDTO.Builder().funcName("init").params(new String[] {"a", "2300", "b", "2400"}).chaincodeID(chaincodeID).policyPath(cxtPath + "policy/chaincodeendorsementpolicy.yaml").build();
//        ExecuteCCDTO initCCDTO = new ExecuteCCDTO.Builder().funcName("init").params(new String[] {"a", "2300", "b", "2400"}).chaincodeID(chaincodeID).build();
        ApiHandler.initializeChainCode(client, channel, initCCDTO);



        // 5. 查询智能合约, 账户b的余额
        ExecuteCCDTO querybCCDTO = new ExecuteCCDTO.Builder().funcName("query").params(new String[] {"b"}).chaincodeID(chaincodeID).build();
        ApiHandler.queryChainCode(client, channel, querybCCDTO);



        // 6. 转账, 账户a转7 RMVB到账户b
        ExecuteCCDTO invokeCCDTO = new ExecuteCCDTO.Builder().funcName("invoke").params(new String[] {"a", "b", "7"}).chaincodeID(chaincodeID).build();
        ApiHandler.invokeChainCode(client, channel, invokeCCDTO);



        // 7. 查询智能合约, 账户b的余额
        ApiHandler.queryChainCode(client, channel, querybCCDTO);



        // 8. 查询智能合约, 账户a的余额
        ExecuteCCDTO queryaCCDTO = new ExecuteCCDTO.Builder().funcName("query").params(new String[] {"a"}).chaincodeID(chaincodeID).build();
        ApiHandler.queryChainCode(client, channel, queryaCCDTO);

    }

}