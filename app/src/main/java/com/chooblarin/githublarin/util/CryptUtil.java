package com.chooblarin.githublarin.util;

import android.content.Context;
import android.support.annotation.Nullable;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

import java.io.IOException;

public class CryptUtil {

    @Nullable
    public static String encrypt(Context context, String name, String planeText) {
        Crypto crypto = getCrypto(context);
        if (null != crypto) {
            try {
                byte[] encrypted = crypto
                        .encrypt(planeText.getBytes("utf-8"), new Entity(name));
                return new String(encrypted, "utf-8");

            } catch (KeyChainException | CryptoInitializationException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    public static String decrypt(Context context, String name, String cipherText) {
        Crypto crypto = getCrypto(context);
        if (null != crypto) {
            try {
                byte[] decrypted = crypto.decrypt(cipherText.getBytes("utf-8"), new Entity(name));
                return new String(decrypted, "utf-8");

            } catch (KeyChainException | CryptoInitializationException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    private static Crypto getCrypto(Context context) {
        Crypto crypto = new Crypto(
                new SharedPrefsBackedKeyChain(context),
                new SystemNativeCryptoLibrary());
        return crypto.isAvailable() ? crypto : null;
    }
}
