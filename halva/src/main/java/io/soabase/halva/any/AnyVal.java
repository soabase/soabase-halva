package io.soabase.halva.any;

public abstract class AnyVal<T> implements Any<T>
{
    private final T matchValue;
    private T value;
    private final InternalType internalType;
    private final boolean isSettable;

    public static <T> AnyVal<T> lit(T matchValue)
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
        this.internalType = InternalType.getInternalType(getClass(), throwIfMisspecified);
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
        return isSettable ? canSetExact(value) : matches(value);
    }

    public boolean canSet(T value, boolean exact)
    {
        if ( isSettable )
        {
            return exact ? canSetExact(value) : canSetLoose(value);
        }
        return matches(value);
    }

    boolean canSetLoose(T value)
    {
        if ( internalType != null )
        {
            try
            {
                internalType.rawType.cast(value);
                return true;
            }
            catch ( ClassCastException dummy )
            {
                // dummy
            }
            return false;
        }
        return false;
    }

    boolean canSetExact(T value)
    {
        if ( internalType != null )
        {
            try
            {
                InternalType valueType = InternalType.getInternalType(value.getClass(), false);
                return internalType.isAssignableFrom(valueType);
            }
            catch ( ClassCastException dummy )
            {
                // dummy
            }
        }
        return false;
    }

    private boolean matches(T value)
    {
        return (matchValue != null) && matchValue.equals(value);
    }
}
