package io.soabase.halva.any;

import io.soabase.halva.tuple.ClassTuple;
import io.soabase.halva.tuple.Tuple;

public abstract class AnyClassTuple<T> extends AnyVal<T> implements ClassTuple
{
    private final Tuple tuple;

    protected AnyClassTuple(Tuple tuple)
    {
        super(null, true, false);
        this.tuple = tuple;
    }

    @Override
    public boolean canSet(T value)
    {
        return (value instanceof ClassTuple) && super.canSet(value);
    }

    @Override
    public final Tuple tuple()
    {
        return tuple;
    }
}
