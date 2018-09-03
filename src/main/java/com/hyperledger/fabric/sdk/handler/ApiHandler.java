package com.hyperledger.fabric.sdk.handler;

import static com.hyperledger.fabric.sdk.logger.Logger.*;
import static com.hyperledger.fabric.sdk.utils.FileUtils.getFile;
import static com.hyperledger.fabric.sdk.utils.FileUtils.getPrivateKeyFromBytes;
import static com.hyperledger.fabric.sdk.common.Constants.*;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.hyperledger.fabric.sdk.entity.dto.EnrollmentDTO;
import com.hyperledger.fabric.sdk.entity.dto.UserContextDTO;
import com.hyperledger.fabric.sdk.entity.dto.api.BuildClientDTO;
import com.hyperledger.fabric.sdk.test.SDKClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PrivateKey;

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
     * 构建客户端实例
     * @param buildClientDTO {@link BuildClientDTO}
     * @return HFClient
     * */
    private static HFClient clientBuild(BuildClientDTO buildClientDTO) throws Exception {
        info("构建Hyperledger Fabric客户端实例 Start...");
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
        info("构建Hyperledger Fabric客户端实例 End!!!");
        return client;
    }


    public static void main(String[] args) throws Exception {
        String mspPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = clientBuild(buildClientDTO);

        Channel channel = client.deSerializeChannel(jedis.get("mychannel".getBytes()));
        channel.initialize();
        SDKClient.queryChainCode(client, channel);
        SDKClient.invokeChainCode(client, channel);
        SDKClient.queryChainCode(client, channel);
    }

}