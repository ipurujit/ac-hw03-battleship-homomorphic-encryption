package org.finki.crypto.puru.helper;


import lombok.NoArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.finki.crypto.puru.exception.PrivateKeyLoadException;
import org.finki.crypto.puru.exception.X509CertificateLoadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;

import static org.finki.crypto.puru.helper.Constants.CRYPTO_PROVIDER;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class LoadKeyFromDisk {

    private static final JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(CRYPTO_PROVIDER);
    private static CertificateFactory certFactory;

    public static CertificateFactory getCertFactory() throws CertificateException, NoSuchProviderException {
        if (certFactory == null) {
            certFactory = CertificateFactory.getInstance("X.509", CRYPTO_PROVIDER);
        }
        return certFactory;
    }


    public static PrivateKey loadPrivateKey(String name) {
        try (InputStream input = LoadKeyFromDisk.class.getResourceAsStream(String.format("/keys/%s/key.pem", name))) {
            InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(input));
            PEMParser pemParser = new PEMParser(reader);
            Object obj = pemParser.readObject();
            if (obj instanceof PrivateKeyInfo pki) {
                // PKCS#8 format
                return converter.getPrivateKey(pki);
            } else {
                throw new PrivateKeyLoadException("Unsupported private key format", null);
            }
        } catch (IOException e) {
            throw new PrivateKeyLoadException("Failed to read private key", e);
        }
    }

    public static X509Certificate loadX509Certificate(String name) {
        try (InputStream input = LoadKeyFromDisk.class.getResourceAsStream(String.format("/keys/%s/cert.pem", name))) {
            return (X509Certificate) getCertFactory().generateCertificate(input);
        } catch (CertificateException | NoSuchProviderException e) {
            throw new X509CertificateLoadException("Failed to load certificate factory", e);
        } catch (IOException e) {
            throw new X509CertificateLoadException("Failed to read certificate file", e);
        }
    }

}
