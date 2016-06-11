package com.company;

import io.soabase.halva.alias.TypeAlias;
import io.soabase.halva.sugar.ConsList;
import io.soabase.halva.tuple.Pair;

@TypeAlias
public interface AliasExample extends ConsList<Pair<String, Integer>>
{
}
