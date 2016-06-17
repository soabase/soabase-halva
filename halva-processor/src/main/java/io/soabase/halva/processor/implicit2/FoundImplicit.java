package io.soabase.halva.processor.implicit2;

import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class FoundImplicit
{
    private final Optional<Element> element;
    private final Set<Element> mapped;

    FoundImplicit(Element element)
    {
        this.element = Optional.of(element);
        this.mapped = new HashSet<>();
    }

    FoundImplicit(Set<Element> mapped)
    {
        this.element = Optional.empty();
        this.mapped = mapped;
    }

    Optional<Element> getElement()
    {
        return element;
    }

    Set<Element> getMapped()
    {
        return mapped;
    }
}
