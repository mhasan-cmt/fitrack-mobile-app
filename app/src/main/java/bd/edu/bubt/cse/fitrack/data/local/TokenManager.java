package bd.edu.bubt.cse.fitrack.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Secure token manager that handles encryption and decryption of authentication tokens
 * using Android KeyStore for enhanced security.
 */
public class TokenManager {
    private static final String TAG = "TokenManager";
    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final String KEY_ALIAS = "FitrackTokenKey";
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USERNAME = "loggedInUsername";
    private static final String KEY_TOKEN_IV = "jwt_token_iv";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private static TokenManager instance;
    private final Context context;
    private final SharedPreferences preferences;

    private TokenManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        try {
            createSecretKey();
        } catch (Exception e) {
            Log.e(TAG, "Error creating secret key", e);
        }
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }

    /**
     * Creates a secret key in the Android KeyStore if it doesn't exist
     */
    private void createSecretKey() throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keyStore.load(null);

            // Return if the key already exists
            if (keyStore.containsAlias(KEY_ALIAS)) {
                return;
            }

            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build());
            keyGenerator.generateKey();
        } catch (KeyStoreException | CertificateException | IOException e) {
            Log.e(TAG, "Error accessing KeyStore", e);
        }
    }

    /**
     * Gets the secret key from the Android KeyStore
     */
    private SecretKey getSecretKey() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
        keyStore.load(null);
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null)).getSecretKey();
    }

    /**
     * Encrypts the token and saves it to SharedPreferences
     */
    public void saveToken(@NonNull String token) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] iv = cipher.getIV();
            byte[] encryptedToken = cipher.doFinal(token.getBytes());

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_TOKEN, Base64.encodeToString(encryptedToken, Base64.DEFAULT));
            editor.putString(KEY_TOKEN_IV, Base64.encodeToString(iv, Base64.DEFAULT));
            editor.apply();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 BadPaddingException | IllegalBlockSizeException | KeyStoreException |
                 CertificateException | IOException | UnrecoverableEntryException e) {
            Log.e(TAG, "Error saving token", e);
            // Fallback to unencrypted storage if encryption fails
            preferences.edit().putString(KEY_TOKEN, token).apply();
        }
    }

    /**
     * Retrieves and decrypts the token from SharedPreferences
     */
    public String getToken() {
        String encryptedToken = preferences.getString(KEY_TOKEN, null);
        String ivString = preferences.getString(KEY_TOKEN_IV, null);

        if (encryptedToken == null) {
            return null;
        }

        // If IV is missing, the token might be stored unencrypted (fallback)
        if (ivString == null) {
            return encryptedToken;
        }

        try {
            byte[] encryptedBytes = Base64.decode(encryptedToken, Base64.DEFAULT);
            byte[] iv = Base64.decode(ivString, Base64.DEFAULT);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving token", e);
            // If decryption fails, return null to force re-authentication
            return null;
        }
    }

    /**
     * Saves the username to SharedPreferences
     */
    public void saveUsername(String username) {
        preferences.edit().putString(KEY_USERNAME, username).apply();
    }

    /**
     * Retrieves the username from SharedPreferences
     */
    public String getUsername() {
        return preferences.getString(KEY_USERNAME, null);
    }

    /**
     * Clears all authentication data
     */
    public void clearAll() {
        preferences.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_TOKEN_IV)
                .remove(KEY_USERNAME)
                .apply();
    }

    /**
     * Checks if a token exists and is valid
     */
    public boolean hasValidToken() {
        return getToken() != null;
    }

    public void saveUserID(@NonNull Long id) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] iv = cipher.getIV();
            byte[] encryptedId = cipher.doFinal(id.toString().getBytes());

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("user_id", Base64.encodeToString(encryptedId, Base64.DEFAULT));
            editor.putString("user_id_iv", Base64.encodeToString(iv, Base64.DEFAULT));
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving user id", e);
            // Fallback to unencrypted storage if encryption fails
            preferences.edit().putString("user_id", id.toString()).apply();
            preferences.edit().remove("user_id_iv").apply();
        }
    }

    public Long getUserID() {
        String encryptedId = preferences.getString("user_id", null);
        String ivString = preferences.getString("user_id_iv", null);

        if (encryptedId == null) {
            return null;
        }

        // If IV is missing, the ID might be stored unencrypted (fallback)
        if (ivString == null) {
            try {
                return Long.parseLong(encryptedId);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing user id", e);
                return null;
            }
        }

        try {
            byte[] encryptedBytes = Base64.decode(encryptedId, Base64.DEFAULT);
            byte[] iv = Base64.decode(ivString, Base64.DEFAULT);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return Long.parseLong(new String(decryptedBytes));
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving user id", e);
            return null;
        }
    }
}