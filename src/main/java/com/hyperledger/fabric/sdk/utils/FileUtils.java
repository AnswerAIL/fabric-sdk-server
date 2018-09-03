package com.hyperledger.fabric.sdk.utils;

import com.hyperledger.fabric.sdk.exception.FabricSDKException;
import com.hyperledger.fabric.sdk.handler.ApiHandler;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;

/**
 * Created by L.Answer on 2018-08-28 17:43
 */
public class FileUtils {


    public static File getFile(String path, String filter) {
        File file = new File(getResourcePath() + path);

        if (file.isFile()) return file;
        else {
            File[] files;
            if (StringUtils.isEmpty(filter)) {
                files = file.listFiles();
            } else {
                files = file.listFiles(((dir, name) -> name.endsWith(filter)));
            }

            if (files == null || files.length <= 0) {
                throw new FabricSDKException("file...");
            }
            return files[0];
        }
    }


    public static PrivateKey getPrivateKeyFromBytes(byte[] data) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        final Reader pemReader = new StringReader(new String(data));
        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }
        return new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
    }


    private static String getResourcePath() {
        return getResourcePath(ApiHandler.class);
    }

    private static String getResourcePath(Class clz) {
        String path = clz.getClassLoader().getResource("").getPath();
        if (StringUtils.isEmpty(path)) {
            throw new FabricSDKException("path is invalid.");
        }
        return path;
    }

}