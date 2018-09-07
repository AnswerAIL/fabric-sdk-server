package com.hyperledger.fabric.sdk.utils;

import static com.hyperledger.fabric.sdk.common.Constants.*;
import static com.hyperledger.fabric.sdk.logger.Logger.info;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by L.Answer on 2018-09-04 14:49
 */
public class HFSDKUtils {

    public static byte[] calculateBlockHash(long blockNumber, byte[] previousHash, byte[] dataHash) throws Exception {
        if (previousHash == null) {
            throw new InvalidArgumentException("previousHash parameter is null.");
        }
        if (dataHash == null) {
            throw new InvalidArgumentException("dataHash parameter is null.");
        }
        CryptoSuite suite = CryptoSuite.Factory.getCryptoSuite();

        ByteArrayOutputStream s = new ByteArrayOutputStream();
        DERSequenceGenerator seq = new DERSequenceGenerator(s);
        seq.addObject(new ASN1Integer(blockNumber));
        seq.addObject(new DEROctetString(previousHash));
        seq.addObject(new DEROctetString(dataHash));
        seq.close();
        return suite.hash(s.toByteArray());
    }



    /**
     * 序列化通道到本地磁盘文件
     * @param channel 通道对象
     * @throws IOException, InvalidArgumentException
     * */
    public static void serialize2File(Channel channel) throws IOException, InvalidArgumentException {
        serialize2File(channel, "sf");
    }


    /**
     * 序列化通道到本地磁盘文件
     * @param channel 通道对象
     * @param fileSuffix 文件后缀, 默认: sf
     * @throws IOException, InvalidArgumentException
     * */
    public static void serialize2File(Channel channel, String fileSuffix) throws IOException, InvalidArgumentException {
        if (SERIAL_CHANNEL_TO_FILE) {
            String path = CHANNEL_SERIAL_PATH;
            if (StringUtils.isEmpty(path)) {
                path = System.getProperty("user.dir");
            }
            if (!path.endsWith(String.valueOf(File.separatorChar))) {
                path = path + String.valueOf(File.separatorChar);
            }
            path = path + channel.getName() + "." + fileSuffix;
            File file = new File(path);
            channel.serializeChannel(file);
            info("serialize channel %s to file, file storage path: %s.", channel.getName(), path);
        }
    }



    /**
     * 反序列化磁盘上的通道对象文件
     * @param client 客户端实例
     * @param path 序列化通道文件路径
     * @throws IOException, InvalidArgumentException, ClassNotFoundException
     * */
    public static Channel deserialize4File(HFClient client, String path) throws IOException, InvalidArgumentException, ClassNotFoundException {
        return client.deSerializeChannel(new File(path));
    }
}