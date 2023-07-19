package com.ngxqt.mdm.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.widget.Toast
import java.security.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class KeyStoreManager(private val context: Context) {
    private lateinit var keyStore: KeyStore
    private lateinit var keyPair: KeyPair

    companion object {
        const val ALIAS_TOKEN = "alias_token"

        private const val TAG = "KeyStoreManager"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_RSA
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_ECB
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

    init {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
        } catch (e: Exception) {
            LogUtils.d("$TAG: ${e.message}")
        }
    }

    fun createKey(alias: String) {
        try {
            if (!keyStore.containsAlias(alias)) {
                val keyPairGenerator =
                    KeyPairGenerator.getInstance(ALGORITHM, "AndroidKeyStore")
                val parameterSpec = KeyGenParameterSpec.Builder(
                    alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setEncryptionPaddings(PADDING)
                    .setDigests(KeyProperties.DIGEST_SHA1)
                    .build()
                keyPairGenerator.initialize(parameterSpec)
                keyPair = keyPairGenerator.genKeyPair()
            } else Toast.makeText(context, "Alias exist!!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            LogUtils.d("$TAG: ${e.message}")
        }
    }

    fun getKeyInfo(alias: String): String {
        //val privateKey: PrivateKey = (keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry).privateKey
        val cert = keyStore.getCertificate(alias)
        val publicKey = cert.publicKey

        val publicKeyBytes: ByteArray = Base64.encode(publicKey.encoded, Base64.DEFAULT)
        val pubKeyString = String(publicKeyBytes)

        //val privateKeyBytes: ByteArray = Base64.encode(privateKey.encoded, Base64.DEFAULT)
        //val priKeyString = String(privateKeyBytes)
        return pubKeyString
    }

    /** Mã hóa và giải mã với dữ liệu dài*/
    fun encryptLongString(clearText: String, alias: String): String {
        val publicKey = keyStore.getCertificate(alias).publicKey
        val symmetricKey = generateSymmetricKey()
        val encryptedSymmetricKey = encryptSymmetricKey(symmetricKey, publicKey)
        val encryptedData = encryptData(clearText.toByteArray(Charsets.UTF_8), symmetricKey)

        val combined = ByteArray(encryptedSymmetricKey.size + encryptedData.size)
        System.arraycopy(encryptedSymmetricKey, 0, combined, 0, encryptedSymmetricKey.size)
        System.arraycopy(encryptedData, 0, combined, encryptedSymmetricKey.size, encryptedData.size)

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decryptLongString(encryptedText: String?, alias: String?): String {
        LogUtils.d("encryptedText = ${encryptedText}, alias = ${alias}")
        val privateKey = keyStore.getKey(alias, null) as PrivateKey
        val combined = Base64.decode(encryptedText, Base64.NO_WRAP)
        val encryptedSymmetricKey = combined.copyOfRange(0, 256)
        val encryptedData = combined.copyOfRange(256, combined.size)

        val symmetricKey = decryptSymmetricKey(encryptedSymmetricKey, privateKey)
        val decryptedData = decryptData(encryptedData, symmetricKey)

        return String(decryptedData, Charsets.UTF_8)
    }

    private fun generateSymmetricKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }

    private fun encryptSymmetricKey(symmetricKey: SecretKey, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(symmetricKey.encoded)
    }

    private fun decryptSymmetricKey(encryptedSymmetricKey: ByteArray, privateKey: PrivateKey): SecretKey {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedSymmetricKey = cipher.doFinal(encryptedSymmetricKey)
        return SecretKeySpec(decryptedSymmetricKey, "AES")
    }

    private fun encryptData(data: ByteArray, symmetricKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, symmetricKey)
        return cipher.doFinal(data)
    }

    private fun decryptData(encryptedData: ByteArray, symmetricKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, symmetricKey)
        return cipher.doFinal(encryptedData)
    }

    fun getAliases(): String {
        var aliasesString = ""
        val aliases = keyStore.aliases()
        while (aliases.hasMoreElements()) {
            aliasesString += "${aliases.nextElement()}, "
        }
        //textAliases.text = aliasesString
        return aliasesString
    }

    fun deleteKey(alias: String) {
        keyStore.deleteEntry(alias)
    }
}