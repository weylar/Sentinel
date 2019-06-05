package com.android.sentinel.HealthCheck.Sensor;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FINGERPRINT;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestFingerPrint extends AppCompatActivity {

    private static final String KEY_NAME = "yourKey";
    private static final int MY_PERMISSIONS_REQUEST_FINGER = 1;
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView textView;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    TextView explain, prompt, skip, result;
    Button button, settings;
    ProgressBar progress;
    Runnable runnable;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_finger_print);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        explain = findViewById(R.id.explain);
        prompt = findViewById(R.id.prompt);
        button = findViewById(R.id.button);
        settings = findViewById(R.id.settings);
        progress = findViewById(R.id.progress);
        skip = findViewById(R.id.skip);
        result = findViewById(R.id.result);
        textView = findViewById(R.id.text);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS), 0);
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(FINGERPRINT, UNCHECKED, TestFingerPrint.this);
                Intent intent = new Intent(TestFingerPrint.this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);


            //Check whether the device has a fingerprint sensor//
            if (!fingerprintManager.isHardwareDetected()) {
                prompt.setText(getResources().getString(R.string.not_support_fingerprint));
                explain.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
            }

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.USE_FINGERPRINT}, MY_PERMISSIONS_REQUEST_FINGER);
            }

            //Check that the user has registered at least one fingerprint//
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                prompt.setText(getResources().getString(R.string.no_finger_registered));
                explain.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
                settings.setVisibility(View.VISIBLE);
            }

            if (!keyguardManager.isKeyguardSecure()) {
                prompt.setText(getResources().getString(R.string.enable_lock));
                explain.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
                settings.setVisibility(View.VISIBLE);
            } else {
                try {
                    generateKey();
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }

                if (initCipher()) {

                    timer(10000);
                    progress.setVisibility(View.VISIBLE);
                    runProgress();
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }
        }
    }


    private void timer(long delayMillis) {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                failAction();
            }
        };

        handler.postDelayed(runnable, delayMillis);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void runProgress() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i <= 10; i++) {
                    if (i > 0) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    progress.setMax(10);
                    progress.setProgress(i);

                }
            }
        };
        thread.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(FINGERPRINT, UNCHECKED, this);
                Intent intent = new Intent(this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setDefaults(FINGERPRINT, UNCHECKED, this);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //Generate the key//
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    //Create a new method that we’ll use to initialize our cipher//
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //Return false if cipher initialization failed//
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

        private CancellationSignal cancellationSignal;
        private Context context;

        public FingerprintHandler(Context mContext) {
            context = mContext;
        }

        @SuppressLint("MissingPermission")
        public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

            cancellationSignal = new CancellationSignal();
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            prompt.setText("Authentication error: " + errString);
            explain.setVisibility(View.GONE);
            setDefaults(FINGERPRINT, FAILED, TestFingerPrint.this);
            result.setText("FAIL");
            result.setTextColor(getResources().getColor(R.color.colorPrimary));
            skip.setVisibility(View.GONE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDefaults(FINGERPRINT, FAILED, TestFingerPrint.this);
                    finish();
                }
            });
        }

        @Override
        public void onAuthenticationFailed() {
            passAction();
            prompt.setText("Fingerprint detected but doesn't match any registered print on device");
            prompt.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            explain.setVisibility(View.GONE);
            prompt.setText("Authentication help: " + helpString);
            skip.setVisibility(View.GONE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDefaults(FINGERPRINT, UNCHECKED, TestFingerPrint.this);
                    finish();
                }
            });

        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult results) {
            passAction();
        }

    }

    private void passAction() {
        explain.setVisibility(View.GONE);
        handler.removeCallbacks(runnable);
        progress.setVisibility(View.GONE);
        setDefaults(FINGERPRINT, SUCCESS, TestFingerPrint.this);
        result.setText("PASS");
        result.setTextColor(getResources().getColor(R.color.green));
        skip.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(FINGERPRINT, SUCCESS, TestFingerPrint.this);
                finish();
            }
        });
    }

    private void failAction() {
        explain.setVisibility(View.GONE);
        result.setText("FAIL");
        progress.setVisibility(View.GONE);
        result.setTextColor(getResources().getColor(R.color.colorPrimary));
        button.setVisibility(View.VISIBLE);
        skip.setVisibility(View.GONE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(FINGERPRINT, FAILED, TestFingerPrint.this);
                finish();
            }
        });
    }
}




