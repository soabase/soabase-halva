package io.soabase.halva.processor.implicit;

import com.squareup.javapoet.ClassName;
import javax.lang.model.type.DeclaredType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GenericMapContext
{
    private final Map<String, List<DeclaredType>> builtItems = new HashMap<>();

    boolean mapClassNeedsBuilding(ClassName className)
    {
        return builtItems.containsKey(className.toString());
    }

    void set(ClassName className, List<DeclaredType> parameterSpecs)
    {
        builtItems.put(className.toString(), Collections.unmodifiableList(new ArrayList<>(parameterSpecs)));
    }

    List<DeclaredType> getParameterSpecs(ClassName className)
    {
        List<DeclaredType> typeList = builtItems.get(className.toString());
        return (typeList != null) ? typeList : new ArrayList<>();
    }
}
