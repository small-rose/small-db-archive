package com.small.archive.utils;

import com.small.archive.core.constant.ArchiveConstant;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ DigestUtils ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/12 012 19:19
 * @Version: v1.0
 */
public class DigestUtils {



    public static Map<String, String> messageDigestConvert(List<Map<String, Object>> sourceList, String primaryKey) throws NoSuchAlgorithmException {
        HashMap result = new HashMap(sourceList.size());
        for (Map<String, Object> line : sourceList) {
            String digest = calculateDigest(line.values().toString());
            String primary = line.keySet().stream().filter(s -> s.equalsIgnoreCase(primaryKey)).findFirst().get();
            result.put(digest, line.get(primary));
        }
        return result;
    }


    /**
     * 计算数据行的摘要值
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String calculateDigest(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] saltData = addSalt(data.getBytes(), ArchiveConstant.SALT.getBytes());
        byte[] digest = md.digest(saltData);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    private static byte[] addSalt(byte[] input, byte[] salt) {
        byte[] saltedInput = new byte[input.length + salt.length];
        System.arraycopy(salt, 0, saltedInput, 0, salt.length);
        System.arraycopy(input, 0, saltedInput, salt.length, input.length);
        return saltedInput;
    }
}
