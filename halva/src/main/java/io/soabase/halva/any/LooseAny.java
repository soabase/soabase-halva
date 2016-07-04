package io.soabase.halva.any;

class LooseAny<T> extends AnyVal<T>
{
    private final AnyVal<T> val;

    LooseAny(AnyVal<T> val)
    {
        super(null, true, false);
        this.val = val;
    }

    @Override
    public T val()
    {
        return val.val();
    }

    @Override
    public void set(T value)
    {
        val.set(value);
    }

    @Override
    public boolean canSet(T value)
    {
        return val.canSet(value, false);
    }

    @Override
    boolean canSetLoose(T value)
    {
        return val.canSet(value, false);
    }

    @Override
    boolean canSetExact(T value)
    {
        throw new UnsupportedOperationException();
    }
}
