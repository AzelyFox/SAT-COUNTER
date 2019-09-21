package kr.devx.satcounter.Util;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

//지문인식 기능은 6.0 이상부터 지원되어 실제 우리 어플에서 사용가능하다는 의미로 선언
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerPrint {

    private static final String KeyName = "satKeyPassword";
    private static final String AndroidKeyStore = "AndroidKeyStore";

    private Context mContext;
    private FingerprintManager fingerprintManager;
    private FingerprintManager.CryptoObject cryptoObject;
    private CancellationSignal cancellationSignal;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;

    //생성자
    public FingerPrint(Context mContext) {
        this.mContext = mContext;
        setKeyStore();
    }

    //콜백 초기화 메소드
    public void initialize(FingerprintManager.AuthenticationCallback callback) {
        cancellationSignal = new CancellationSignal(); //취소신호
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, callback, null);
    }

    public void cancelAuthentication() {
        if (Build.VERSION.SDK_INT < 23) return;
        if (cancellationSignal != null) cancellationSignal.cancel();
    }

    //하드웨어 지원 가능 여부
    public boolean isFingerHarWare(){
        return fingerprintManager.isHardwareDetected();
    }

    //하드웨어 현재 지문 여부
    public boolean isFingerPassCode(){
        return fingerprintManager.hasEnrolledFingerprints();
    }

    //KeyStore 세팅
    private void setKeyStore() {
        //FingerPrint Manager Setting
        fingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
        cryptoObject = new FingerprintManager.CryptoObject(cipher);

        //keyStore //표준 android 키 저장소 컨테이너 식별자 ("AndroidKeyStore")를 사용하여 키 저장소에 대한 참조를 가져옴
        try {
            keyStore = KeyStore.getInstance(AndroidKeyStore);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //keyGenerator
        try {
            //key 생성
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //keyGenerator initalize //새 개인 키 생성
        try {
            //빈 ketStore 초기화
            keyStore.load(null);

            //keyGenerator 초기화
            keyGenerator.init(new KeyGenParameterSpec.Builder(KeyName, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //key 생성
            keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // cipher initalize
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // cipher set Key
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KeyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (KeyPermanentlyInvalidatedException e) {
            e.printStackTrace();
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

}
