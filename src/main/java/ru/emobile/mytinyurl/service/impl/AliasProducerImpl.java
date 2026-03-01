package ru.emobile.mytinyurl.service.impl;

import org.springframework.stereotype.Component;
import ru.emobile.mytinyurl.service.AliasProducer;

@Component
public class AliasProducerImpl implements AliasProducer {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length();
    private static final String ZERO = "0";


    @Override
    public String getAlias(String url) {
        int hash = url.hashCode();

        long positive = Integer.toUnsignedLong(hash);

        String base62 = toBase62(positive);

        String core = base62.length() > 8
                ? base62.substring(0, 8)
                : base62;

        if (core.length() < 4) {
            core = String.format("%-4s", core).replace(' ', '0');
        }

        long timeHash = System.currentTimeMillis() ^ positive;
        String timePart = toBase62(timeHash);

        String suffix = timePart.substring(0, Math.min(2, timePart.length()));

        String alias = core;
        if (alias.length() + suffix.length() <= 8) {
            alias = alias + suffix;
        }

        return alias.substring(0, Math.min(8, alias.length()));

    }

    private String toBase62(long value) {
        if (value == 0) {
            return ZERO;
        }
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            int idx = (int) (value % BASE);
            sb.append(ALPHABET.charAt(idx));
            value /= BASE;
        }
        return sb.reverse().toString();
    }

}
