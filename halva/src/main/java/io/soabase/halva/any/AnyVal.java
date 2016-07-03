package io.soabase.halva.any;

import static io.soabase.halva.any.AnyType.getInternalType;

public abstract class AnyVal<T> implements Any<T>
{
    private final T matchValue;
    private T value;
    private final AnyType.InternalType internalType;

    public static <T> AnyVal<T> val(T matchValue)
    {
        return new AnyVal<T>(matchValue, false){};
    }

    public static <T> AnyVal<T> any()
    {
        return val(null);
    }

    protected AnyVal()
    {
        this(null, true);
    }

    private AnyVal(T matchValue, boolean throwIfMisspecified)
    {
        this.matchValue = matchValue;
        this.internalType = getInternalType(getClass(), throwIfMisspecified);
    }

    @Override
    public T val()
    {
        return value;
    }

    @Override
    public void set(T value)
    {
        if ( matchValue == null )
        {
            this.value = value;
        }
        // else NOP
    }

    @Override
    public boolean canSet(T value)
    {
        return (matchValue == null) || matchValue.equals(value);
    }

    boolean canSetExact(T value)
    {
        return AnyImpl.canSetExact(value, internalType);
    }
}
