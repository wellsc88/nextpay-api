package com.well.tech.next.pay.common.exceptions;

import java.time.Instant;

public record ApiError(

        int status,

        String error,

        String message,

        String path,

        Instant timestamp
) {
}