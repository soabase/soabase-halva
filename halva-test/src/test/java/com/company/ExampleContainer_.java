package com.company;

import io.soabase.halva.alias.TypeAlias;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.container.TypeContainer;
import io.soabase.halva.sugar.ConsList;
import java.util.List;

@TypeContainer
public interface ExampleContainer_
{
    @TypeAlias
    interface Stack extends ConsList<List<String>>{}

    @CaseClass
    interface MyStack{ExampleContainer.Stack stack(); int value();}
}
