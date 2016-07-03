package io.soabase.halva.any;

import static io.soabase.halva.any.AnyType.getInternalType;

public abstract class Match<T> implements Any<T>
{
    private final T matchValue;
    private T value;
    private final AnyType.InternalType internalType;

    public static <T> Match<T> val(T matchValue)
    {
        return new Match<T>(matchValue, false){};
    }

    protected Match()
    {
        this(null, true);
    }

    private Match(T matchValue, boolean throwIfMisspecified)
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
