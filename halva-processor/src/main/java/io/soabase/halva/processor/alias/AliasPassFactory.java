package io.soabase.halva.processor.alias;

import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import io.soabase.halva.processor.PassFactory;
import io.soabase.halva.processor.WorkItem;
import java.util.List;
import java.util.Optional;

class AliasPassFactory implements PassFactory
{
    @Override
    public Optional<Pass> firstPass(Environment environment, List<WorkItem> workItems)
    {
        return Optional.of(new Pass1Analyze(environment, workItems));
    }
}
