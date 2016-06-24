package com.company;

import io.soabase.halva.comprehension.MonadicFor;
import io.soabase.halva.comprehension.MonadicForWrapper;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@MonadicFor
public class OptionalForFactory implements MonadicForWrapper<Optional>
{
    @SuppressWarnings("unchecked")
    @Override
    public <A> Optional flatMap(Optional m, Function<A, ? extends Optional> flatMapper)
    {
        return m.flatMap(flatMapper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A, B> Optional map(Optional m, Function<A, B> mapper)
    {
        return m.map(mapper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional filter(Optional m, Predicate predicate)
    {
        return m.filter(predicate);
    }
}
