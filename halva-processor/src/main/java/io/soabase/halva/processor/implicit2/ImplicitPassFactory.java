package io.soabase.halva.processor.implicit2;

import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import io.soabase.halva.processor.PassFactory;
import io.soabase.halva.processor.WorkItem;
import java.util.List;
import java.util.Optional;

class ImplicitPassFactory implements PassFactory
{
    @Override
    public Optional<Pass> firstPass(Environment environment, List<WorkItem> workItems)
    {
        return Optional.of(new PassProcessContexts(environment, workItems));
    }
}
