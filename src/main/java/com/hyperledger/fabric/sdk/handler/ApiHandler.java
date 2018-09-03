package com.hyperledger.fabric.sdk.handler;

import static com.hyperledger.fabric.sdk.logger.Logger.*;
import static com.hyperledger.fabric.sdk.logger.Logger.debug;
import static com.hyperledger.fabric.sdk.utils.FileUtils.getFile;
import static com.hyperledger.fabric.sdk.utils.FileUtils.getPrivateKeyFromBytes;
import static com.hyperledger.fabric.sdk.common.Constants.*;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.hyperledger.fabric.sdk.entity.dto.EnrollmentDTO;
import com.hyperledger.fabric.sdk.entity.dto.UserContextDTO;
import com.hyperledger.fabric.sdk.entity.dto.api.BuildClientDTO;
import com.hyperledger.fabric.sdk.entity.dto.api.QueryCCDTO;
import com.hyperledger.fabric.sdk.test.SDKClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by answer on 2018-09-03 11:16
 */
public class ApiHandler {
    private static Jedis jedis = new Jedis(JEDIS_IP);
    static {
        jedis.select(JEDIS_INDEX);

        if (StringUtils.isNotEmpty(JEDIS_PASSWD)) {
            jedis.auth(JEDIS_PASSWD);
        }
    }



    /**
     * Answer - 构建客户端实例
     * @param buildClientDTO {@link BuildClientDTO}
     * @return HFClient
     * */
    public static HFClient clientBuild(BuildClientDTO buildClientDTO) throws Exception {
        debug("构建Hyperledger Fabric客户端实例 Start...");
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        String mspPath = buildClientDTO.getMspPath();

        File certFile, keyFile;
        if (StringUtils.isNotEmpty(mspPath)) {
            certFile = getFile(mspPath + "signcerts", null);
            keyFile = getFile(mspPath + "keystore", "_sk");
        } else {
            certFile = new File(buildClientDTO.getCertPath());
            keyFile = new File(buildClientDTO.getKeyPath());
        }

        InputStream certFileIS = new FileInputStream(certFile);
        String cert = new String(IOUtils.toByteArray(certFileIS), UTF_8);

        InputStream keyFileIS = new FileInputStream(keyFile);
        PrivateKey key = getPrivateKeyFromBytes(IOUtils.toByteArray(keyFileIS));

        String name = buildClientDTO.getName();
        String mspid = buildClientDTO.getMspId();
        Enrollment enrollmentDTO = new EnrollmentDTO(cert, key);

        User user = new UserContextDTO(name, mspid, enrollmentDTO);
        client.setUserContext(user);
        debug("构建Hyperledger Fabric客户端实例 End!!!");
        return client;
    }


    /**
     * Answer - 查询智能合约
     * @param client 客户端实例
     * @param channel 通道对象
     * @param queryCCDTO {@link QueryCCDTO}
     * */
    public static void queryChainCode(HFClient client, Channel channel, QueryCCDTO queryCCDTO) throws Exception {
        debug("查询智能合约 Start, channelName: %s, fcn: %s, args: %s", channel.getName(), queryCCDTO.getFuncName(), Arrays.asList(queryCCDTO.getParams()));
        QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(queryCCDTO.getParams());
        queryByChaincodeRequest.setFcn(queryCCDTO.getFuncName());
        queryByChaincodeRequest.setChaincodeID(queryCCDTO.getChaincodeID());

        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());

        checkResult(queryProposals);
        debug("查询智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), queryCCDTO.getFuncName(), Arrays.asList(queryCCDTO.getParams()));
    }


    /**
     * 校验链码响应结果
     * @param proposalResponses 提议响应集
     * @return flag boolean
     * */
    private static boolean checkResult(Collection<ProposalResponse> proposalResponses) {
        boolean flag = true;
        for (ProposalResponse proposalResponse: proposalResponses) {
            String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
            info("response status: %s, isVerified: %b from peer: %s, payload: %s.", proposalResponse.getStatus(), proposalResponse.isVerified(), proposalResponse.getPeer().getName(), payload);

            if (proposalResponse.getStatus() == ProposalResponse.Status.FAILURE) {
                info("failed message: %s, isVerified: %b from peer: %s.", proposalResponse.getMessage(), proposalResponse.isVerified(), proposalResponse.getPeer().getName());
                flag = false;
            }
        }
        return flag;
    }


    public static void main(String[] args) throws Exception {
        String chaincodeName = "mycc";
        String chaincodeVersion = "1.0";
        String chaincodePath = "github.com/chaincode_example02";
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();


        String mspPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = clientBuild(buildClientDTO);

        Channel channel = client.deSerializeChannel(jedis.get("mychannel".getBytes()));
        channel.initialize();

        QueryCCDTO queryCCDTO = new QueryCCDTO.Builder().funcName("query").params(new String[] {"b"}).chaincodeID(chaincodeID).build();
        queryChainCode(client, channel, queryCCDTO);

//        SDKClient.queryChainCode(client, channel);
//        SDKClient.invokeChainCode(client, channel);
//        SDKClient.queryChainCode(client, channel);
    }

}