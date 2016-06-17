package io.soabase.halva.processor;

import java.util.Optional;

public interface Pass
{
    Optional<Pass> process();
}
