package org.finki.crypto.puru.helper;

import lombok.NoArgsConstructor;

import java.security.SecureRandom;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Constants {
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public static final String CRYPTO_PROVIDER = "BC";

    public static final int TOTAL_SHIPS_PER_PLAYER = 5;
    public static final int MAX_LENGTH_PER_SHIP = 5;
    public static final int TOTAL_ROWS_PER_BOARD = 10;
    public static final int TOTAL_COLS_PER_BOARD = 10;
}
