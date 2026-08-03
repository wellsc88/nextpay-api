package com.well.tech.next.pay.service.abstraction;

import javax.crypto.Mac;
import java.security.GeneralSecurityException;

public interface MacFactory {

    Mac create(String algorithm)
            throws GeneralSecurityException;
}