package com.well.tech.next.pay.infrastructure;

import com.well.tech.next.pay.service.abstraction.MacFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import java.security.GeneralSecurityException;

@Component
public class DefaultMacFactory implements MacFactory {

    @Override
    public Mac create(String algorithm)
            throws GeneralSecurityException {

        return Mac.getInstance(algorithm);
    }
}
