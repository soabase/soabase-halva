package io.soabase.halva.any;

import static io.soabase.halva.any.AnyType.getInternalType;

public abstract class AnyVal<T> implements Any<T>
{
    private final T matchValue;
    private T value;
    private final AnyType.InternalType internalType;
    private final boolean isSettable;

    public static <T> AnyVal<T> val(T matchValue)
    {
        return new AnyVal<T>(matchValue, false, false){};
    }

    public static <T> AnyVal<T> any()
    {
        return new AnyVal<T>(null, true, false){};
    }

    protected AnyVal()
    {
        this(null, true, true);
    }

    AnyVal(T matchValue, boolean isSettable, boolean throwIfMisspecified)
    {
        this.matchValue = matchValue;
        this.internalType = getInternalType(getClass(), throwIfMisspecified);
        this.isSettable = isSettable;
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
        return isSettable || ((matchValue != null) && matchValue.equals(value));
    }

    boolean canSetExact(T value)
    {
        return AnyImpl.canSetExact(value, internalType);
    }
}
