package cc.farlanders.config;

public enum Values {
    PRIME_X(0x5205402B9270C86FL),
    PRIME_Y(0x598CD327003817B5L),
    PRIME_Z(0x5BCC226E9FA0BACBL),
    PRIME_W(0x56CC5227E58F554BL),
    HASH_MULTIPLIER(0x53A3F72DEEC546F5L),
    SEED_FLIP_3D(-0x52D547B2E96ED629L),

    ROOT2OVER2(0.7071067811865476),
    SKEW_2D(0.366025403784439),
    UNSKEW_2D(-0.21132486540518713),
    ROOT3OVER3(0.577350269189626),
    FALLBACK_ROTATE3(2.0 / 3.0),
    ROTATE3_ORTHOGONALIZER(-0.21132486540518713),

    SKEW_4D(0.309016994374947f),
    UNSKEW_4D(-0.138196601125011f),

    N_GRADS_2D_EXPONENT(7),
    N_GRADS_3D_EXPONENT(8),
    N_GRADS_4D_EXPONENT(9),
    N_GRADS_2D(1 << 7),
    N_GRADS_3D(1 << 8),
    N_GRADS_4D(1 << 9),

    NORMALIZER_2D(0.05481866495625118),
    NORMALIZER_3D(0.2781926117527186),
    NORMALIZER_4D(0.11127401889945551),

    RSQUARED_2D(2.0f / 3.0f),
    RSQUARED_3D(3.0f / 4.0f),
    RSQUARED_4D(4.0f / 5.0f);

    private final Long longValue;
    private final Double doubleValue;
    private final Float floatValue;
    private final Integer intValue;

    Values(long value) {
        this.longValue = value;
        this.doubleValue = null;
        this.floatValue = null;
        this.intValue = null;
    }

    Values(double value) {
        this.longValue = null;
        this.doubleValue = value;
        this.floatValue = null;
        this.intValue = null;
    }

    Values(float value) {
        this.longValue = null;
        this.doubleValue = null;
        this.floatValue = value;
        this.intValue = null;
    }

    Values(int value) {
        this.longValue = null;
        this.doubleValue = null;
        this.floatValue = null;
        this.intValue = value;
    }

    public Long asLong() {
        return longValue;
    }

    public Double asDouble() {
        return doubleValue;
    }

    public Float asFloat() {
        return floatValue;
    }

    public Integer asInt() {
        return intValue;
    }

    public static Values[] getAllValues() {
        return Values.values();
    }
}
