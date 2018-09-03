package com.hyperledger.fabric.sdk;

import com.alibaba.fastjson.JSON;
import com.hyperledger.fabric.sdk.entity.yaml.tag.User;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

/**
 * Created by L.Answer on 2018-08-31 16:35
 */
public class RedisTest {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1");
//        jedis.auth("");
        jedis.select(0);

        User user = new User();
        user.setName("org1");
        user.setMspid("org1Admin");
        user.setMspPath("/root/home");
        // 把user对象持久化到redis中
        jedis.set(user.getName(), JSON.toJSONString(user));

        String userObj = jedis.get("org11");
        if (StringUtils.isNotEmpty(userObj)) {
            User user1 = JSON.parseObject(userObj, User.class);
            System.out.println(user1.getMspPath());
        }

    }

}