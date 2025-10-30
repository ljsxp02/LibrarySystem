package library.util;

/**
 * Simple password encoder supporting a noop scheme.
 */
public final class PasswordEncoder {
    private static final String NOOP_PREFIX = "{noop}";

    private PasswordEncoder() {
    }

    /**
     * Encodes the raw password using the noop scheme.
     *
     * @param raw raw password
     * @return encoded password
     */
    public static String encode(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("raw password must not be null");
        }
        if (raw.startsWith(NOOP_PREFIX)) {
            return raw;
        }
        return NOOP_PREFIX + raw;
    }

    /**
     * Matches a raw password against an encoded password hash.
     *
     * @param raw  raw password
     * @param hash stored hash
     * @return {@code true} if matches
     */
    public static boolean matches(String raw, String hash) {
        if (raw == null || hash == null) {
            return false;
        }
        if (hash.startsWith(NOOP_PREFIX)) {
            return raw.equals(hash.substring(NOOP_PREFIX.length()));
        }
        return hash.equals(raw);
    }
}
