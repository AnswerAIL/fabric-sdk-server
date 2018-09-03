package com.hyperledger.fabric.sdk.utils;

import com.hyperledger.fabric.sdk.exception.FabricSDKException;
import com.hyperledger.fabric.sdk.test.SDKClient;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created by answer on 2018-08-28 17:43
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


    private static String getResourcePath() {
        return getResourcePath(SDKClient.class);
    }

    private static String getResourcePath(Class clz) {
        String path = clz.getClassLoader().getResource("").getPath();
        if (StringUtils.isEmpty(path)) {
            throw new FabricSDKException();
        }
        return path;
    }

}