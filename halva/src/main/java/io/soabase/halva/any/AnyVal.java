package io.soabase.halva.any;

public abstract class AnyVal<T>
{
    private final T matchValue;
    private T value;
    private final InternalType internalType;
    private final boolean isSettable;

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

    public T val()
    {
        return value;
    }

    public void set(T value)
    {
        if ( matchValue == null )
        {
            this.value = value;
        }
        // else NOP
    }

    public boolean canSet(T value)
    {
        return isSettable ? canSetExact(value) : matches(value);
    }

    boolean canSet(T value, boolean exact)
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
