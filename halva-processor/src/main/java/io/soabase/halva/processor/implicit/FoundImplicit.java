package io.soabase.halva.processor.implicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class FoundImplicit
{
    private final List<ContextItem> items;

    public FoundImplicit()
    {
        this(new ArrayList<>());
    }

    FoundImplicit(ContextItem item)
    {
        this(Collections.singletonList(item));
    }

    FoundImplicit(List<ContextItem> items)
    {
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
    }

    boolean isMultiItem()
    {
        return items.size() > 1;
    }

    boolean hasImplicits()
    {
        return items.size() > 0;
    }

    List<ContextItem> getItems()
    {
        return items;
    }
}
