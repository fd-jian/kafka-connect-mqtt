package com.evokly.kafka.connect.mqtt.ssl;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by booncol on 07.04.2016.
 *
 */
public class SslUtils {

    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    private static final boolean DEVELOPMENT = true;

    /** Create SSLSocketFactory.
     *
     * @param caCrt CA certificate filepath
     * @param crt Client certificate filepath
     * @param key Client key filepath
     * @param password Password
     *
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory getSslSocketFactory(String caCrt,
                                                       String crt,
                                                       String key,
                                                       String password)
            throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
            UnrecoverableKeyException, KeyManagementException {

        SSLContext context = SSLContext.getInstance("TLSv1.2");
        // TODO: allow all certs during development
        if (DEVELOPMENT) { 
            return initDevContext(context);
        }

        char[] passwdChars = password != null && password.length() > 0
                ? password.toCharArray() : "".toCharArray();

        PEMParser parser = null;
        KeyPair keyPair = null;
        if (key != null) {
            // load client private key
            parser = new PEMParser(
                    new InputStreamReader(new FileInputStream(key), Charset.forName("UTF-8"))
                    );

            Object obj = parser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            if (obj instanceof PEMEncryptedKeyPair) {
                PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder()
                        .build(passwdChars);
                converter = new JcaPEMKeyConverter().setProvider("BC");
                keyPair = converter.getKeyPair(((PEMEncryptedKeyPair) obj).decryptKeyPair(decProv));
            } else {
                keyPair = converter.getKeyPair((PEMKeyPair) obj);
            }

            parser.close();
        }

        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        certConverter.setProvider("BC");

        // load CA certificate
        parser = new PEMParser(
                new InputStreamReader(new FileInputStream(caCrt), Charset.forName("UTF-8"))
        );
        X509CertificateHolder caCert = (X509CertificateHolder) parser.readObject();
        parser.close();

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", certConverter.getCertificate(caCert));

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory
                .getDefaultAlgorithm());

        tmf.init(caKs);

        KeyManagerFactory kmf = null;
        if (crt != null) {
            // load client certificate
            parser = new PEMParser(
                    new InputStreamReader(new FileInputStream(crt), Charset.forName("UTF-8"))
                    );

            X509CertificateHolder cert = (X509CertificateHolder) parser.readObject();
            parser.close();

            // Client key and certificates are sent to server so it can authenticate
            // us
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            ks.setCertificateEntry("certificate", certConverter.getCertificate(cert));
            ks.setKeyEntry("private-key", keyPair.getPrivate(), passwdChars,
                    new java.security.cert.Certificate[] { certConverter.getCertificate(cert) });
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory
                    .getDefaultAlgorithm());
            kmf.init(ks, passwdChars);
        }

        // Finally, create SSL socket factory
        //
        context.init(kmf != null ? kmf.getKeyManagers() : null, tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }

    /** Create SSLSocketFactory for development.
     *
     * @param sslContext SSL Context
     *
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory initDevContext(SSLContext sslContext) 
            throws KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] { 
            new X509TrustManager() {     

                public X509Certificate[] getAcceptedIssuers() { 
                    return new X509Certificate[0];
                } 

                public void checkClientTrusted( 
                        X509Certificate[] certs, String authType) {
                        } 

                public void checkServerTrusted( 
                        X509Certificate[] certs, String authType) {
                        }
            } 

        };
        sslContext.init(null, trustAllCerts, new SecureRandom());

        return sslContext.getSocketFactory();
    }

}
