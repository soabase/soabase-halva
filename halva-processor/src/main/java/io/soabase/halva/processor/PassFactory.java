package io.soabase.halva.processor;

import java.util.List;
import java.util.Optional;

public interface PassFactory
{
    Optional<Pass> firstPass(Environment environment, List<WorkItem> workItems);
}
