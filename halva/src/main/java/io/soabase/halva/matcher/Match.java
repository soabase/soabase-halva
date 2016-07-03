package io.soabase.halva.matcher;

import io.soabase.halva.any.Any;

public class Match<T> implements Any<T>
{
    private final T value;
    private final Any<T> any;

    public static <T> Match<T> val(T value)
    {
        return new Match<>(value, null);
    }

    public static <T> Match<T> any(Any<T> value)
    {
        return new Match<>(null, value);
    }

    private Match(T value, Any<T> any)
    {
        this.value = value;
        this.any = any;
    }

    @Override
    public T val()
    {
        return (any != null) ? any.val() : value;
    }

    @Override
    public void set(T value)
    {
        if ( any != null )
        {
            any.set(value);
        }
        // else NOP
    }

    @Override
    public boolean canSet(T value)
    {
        return (any != null) || this.value.equals(value);
    }
}
