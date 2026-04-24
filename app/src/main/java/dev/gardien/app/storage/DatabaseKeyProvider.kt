package dev.gardien.app.storage

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class DatabaseKeyProvider(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getOrCreatePassphrase(): ByteArray {
        val storedIv = prefs.getString(KEY_IV, null)
        val storedCiphertext = prefs.getString(KEY_CIPHERTEXT, null)

        if (storedIv != null && storedCiphertext != null) {
            return decrypt(
                iv = Base64.decode(storedIv, Base64.NO_WRAP),
                ciphertext = Base64.decode(storedCiphertext, Base64.NO_WRAP),
            )
        }

        val passphrase = ByteArray(PASSPHRASE_BYTES).also { SecureRandom().nextBytes(it) }
        val encrypted = encrypt(passphrase)
        prefs.edit()
            .putString(KEY_IV, Base64.encodeToString(encrypted.iv, Base64.NO_WRAP))
            .putString(KEY_CIPHERTEXT, Base64.encodeToString(encrypted.ciphertext, Base64.NO_WRAP))
            .apply()
        return passphrase
    }

    private fun encrypt(passphrase: ByteArray): EncryptedValue {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKeystoreKey())
        return EncryptedValue(
            iv = cipher.iv,
            ciphertext = cipher.doFinal(passphrase),
        )
    }

    private fun decrypt(iv: ByteArray, ciphertext: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKeystoreKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
        return cipher.doFinal(ciphertext)
    }

    private fun getOrCreateKeystoreKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        keyStore.getKey(KEY_ALIAS, null)?.let { return it as SecretKey }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build(),
        )
        return generator.generateKey()
    }

    private data class EncryptedValue(
        val iv: ByteArray,
        val ciphertext: ByteArray,
    )

    private companion object {
        const val PREFS = "gardien_database_key"
        const val KEY_ALIAS = "gardien_sqlcipher_key"
        const val KEY_IV = "wrapped_key_iv"
        const val KEY_CIPHERTEXT = "wrapped_key_ciphertext"
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_TAG_BITS = 128
        const val PASSPHRASE_BYTES = 32
    }
}
