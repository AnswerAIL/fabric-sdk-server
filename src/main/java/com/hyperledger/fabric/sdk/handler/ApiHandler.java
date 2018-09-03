package com.hyperledger.fabric.sdk.handler;

import static com.hyperledger.fabric.sdk.logger.Logger.*;
import static com.hyperledger.fabric.sdk.utils.FileUtils.getFile;
import static com.hyperledger.fabric.sdk.utils.FileUtils.getPrivateKeyFromBytes;

import com.hyperledger.fabric.sdk.entity.dto.EnrollmentDTO;
import com.hyperledger.fabric.sdk.entity.dto.UserContextDTO;
import com.hyperledger.fabric.sdk.entity.dto.api.BuildClientDTO;
import com.hyperledger.fabric.sdk.test.SDKClient;
import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import redis.clients.jedis.Jedis;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

/**
 * Created by answer on 2018-09-03 11:16
 */
public class ApiHandler {





    private static HFClient createChannel(BuildClientDTO buildClientDTO) throws Exception {
        info("准备初始化客户端实例...");
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        String mspPath = buildClientDTO.getMspPath();
        InputStream certFileIS = new FileInputStream(getFile(mspPath + "signcerts", null));
        String cert = new String(IOUtils.toByteArray(certFileIS), StandardCharsets.UTF_8);

        InputStream keyFileIS = new FileInputStream(getFile(mspPath + "keystore", "_sk"));
        PrivateKey key = getPrivateKeyFromBytes(IOUtils.toByteArray(keyFileIS));

        String name = buildClientDTO.getName();
        String mspid = buildClientDTO.getMspId();
        Enrollment enrollmentDTO = new EnrollmentDTO(cert, key);

        User user = new UserContextDTO(name, mspid, enrollmentDTO);
        client.setUserContext(user);

        return client;
    }


    public static void main(String[] args) throws Exception {
        String mspPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com")
                .mspId("Org1MSP")
                .mspPath(mspPath)
                .build();
        HFClient client = createChannel(buildClientDTO);

        /*HFClient client = SDKClient.clientBuild();*/

        Jedis jedis = new Jedis("127.0.0.1");
        jedis.select(0);

        Channel channel = client.deSerializeChannel(jedis.get("mychannel".getBytes()));
        System.out.println(channel.getName());
        channel.initialize();
        SDKClient.queryChainCode(client, channel);
        SDKClient.invokeChainCode(client, channel);
        SDKClient.queryChainCode(client, channel);
    }

}