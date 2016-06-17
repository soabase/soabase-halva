package io.soabase.halva.processor.implicit;

import javax.lang.model.element.VariableElement;

class ImplicitParameter
{
    private final VariableElement parameter;
    private final FoundImplicit foundImplicit;

    ImplicitParameter(VariableElement parameter, FoundImplicit foundImplicit)
    {
        this.parameter = parameter;
        this.foundImplicit = foundImplicit;
    }

    VariableElement getParameter()
    {
        return parameter;
    }

    FoundImplicit getFoundImplicit()
    {
        return foundImplicit;
    }
}
