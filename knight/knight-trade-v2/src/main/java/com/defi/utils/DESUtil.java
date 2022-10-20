package com.defi.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Created by kuangxiaoguo on 16/9/11.
 * <p>
 * DES加密工具类
 */
@Component
public class DESUtil {

    @Value("${wallet.aes.key}")
    public String aesKey;//加密key

    /**
     * 解密
     *
     * @return 返回解密内容
     */
    public String decrypt(String data) {
        return DecryptCoder.decrypt(data,aesKey);
    }

    /**
     * 加密
     *
     * @return 返回加密内容
     */
    public String encrypt(String data) {
        return EncryptCoder.encrypt(data, aesKey);
    }


    public static void main(String[] args) {
        //String hsr ="12345678";
        /*String ltcdecrypt = EncryptCoder.encrypt("You385593", "!q@i#q$i%l^i&u*(");
        System.out.println(ltcdecrypt);*/

        String keyStore = "";
        String keyStores = DecryptCoder.decrypt(keyStore,"13EQ@i#q$i%lo^i&u*(;5");
        System.out.println(keyStores);

        String pwd= "";
        String pwds = DecryptCoder.decrypt(pwd,"13EQ@i#q$i%lo^i&u*(;5");
        System.out.println(pwds);


    }
}

