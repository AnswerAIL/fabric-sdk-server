package com.hyperledger.fabric.sdk.handler;

import com.hyperledger.fabric.sdk.entity.dto.api.BuildClientDTO;
import com.hyperledger.fabric.sdk.entity.dto.api.ExecuteCCDTO;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;

/**
 * Created by L.Answer on 2018-09-03 17:42
 *
 * SDK-API 测试用例
 *
 * 测试时需要把resources目录赋值到test目录下
 */
public class APITest {

    public static void main(String[] args) throws Exception {
        String chaincodeVersion = "1.0";
        String chaincodePath = "github.com/chaincode_example02";
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName("mycc").setVersion(chaincodeVersion).setPath(chaincodePath).build();

        String mspPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();

        HFClient client = ApiHandler.clientBuild(buildClientDTO);
        Channel channel = ApiHandler.createChannel(client);

        ExecuteCCDTO querybCCDTO = new ExecuteCCDTO.Builder().funcName("query").params(new String[] {"b"}).chaincodeID(chaincodeID).build();
        ApiHandler.queryChainCode(client, channel, querybCCDTO);

        ExecuteCCDTO invokeCCDTO = new ExecuteCCDTO.Builder().funcName("invoke").params(new String[] {"a", "b", "7"}).chaincodeID(chaincodeID).build();
        ApiHandler.invokeChainCode(client, channel, invokeCCDTO);

        ApiHandler.queryChainCode(client, channel, querybCCDTO);

        ExecuteCCDTO queryaCCDTO = new ExecuteCCDTO.Builder().funcName("query").params(new String[] {"a"}).chaincodeID(chaincodeID).build();
        ApiHandler.invokeChainCode(client, channel, queryaCCDTO);

    }

}