package com.chooblarin.githublarin.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Base64;

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
        String encryptedString = "";
        if (null != crypto) {
            try {
                byte[] encrypted = crypto
                        .encrypt(planeText.getBytes("utf-8"), new Entity(name));
                encryptedString = Base64.encodeToString(encrypted, Base64.DEFAULT);

            } catch (KeyChainException | CryptoInitializationException | IOException e) {
                e.printStackTrace();
            }
        }
        return encryptedString;
    }

    @Nullable
    public static String decrypt(Context context, String name, String cipherText) {
        Crypto crypto = getCrypto(context);
        String decryptedString = "";
        if (null != crypto) {
            try {
                byte[] byteText = Base64.decode(cipherText, Base64.DEFAULT);
                byte[] decrypted = crypto.decrypt(byteText, new Entity(name));
                decryptedString = new String(decrypted, "utf-8");

            } catch (KeyChainException | CryptoInitializationException | IOException e) {
                e.printStackTrace();
            }
        }
        return decryptedString;
    }

    @Nullable
    private static Crypto getCrypto(Context context) {
        Crypto crypto = new Crypto(
                new SharedPrefsBackedKeyChain(context),
                new SystemNativeCryptoLibrary());
        return crypto.isAvailable() ? crypto : null;
    }
}
