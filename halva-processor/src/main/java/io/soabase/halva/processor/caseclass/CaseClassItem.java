package io.soabase.halva.processor.caseclass;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

class CaseClassItem
{
    private final Optional<String> name;
    private final Optional<TypeMirror> type;
    private final Optional<TypeMirror> erasedType;
    private final boolean hasDefaultValue;
    private final boolean mutable;

    CaseClassItem()
    {
        name = Optional.empty();
        type = Optional.empty();
        erasedType = Optional.empty();
        hasDefaultValue = false;
        mutable = false;
    }

    CaseClassItem(String name, TypeMirror type, TypeMirror erasedType, boolean hasDefaultValue, boolean mutable)
    {
        this.name = Optional.of(name);
        this.type = Optional.of(type);
        this.erasedType = Optional.of(erasedType);
        this.hasDefaultValue = hasDefaultValue;
        this.mutable = mutable;

        if ( type.getKind() == TypeKind.WILDCARD )
        {
            System.out.println();
        }
    }

    String getName()
    {
        return name.orElseThrow(() -> new RuntimeException("Error CaseClassField accessed"));
    }

    TypeMirror getType()
    {
        return type.orElseThrow(() -> new RuntimeException("Error/Ignore CaseClassField accessed"));
    }

    TypeMirror getErasedType()
    {
        return erasedType.orElseThrow(() -> new RuntimeException("Error/Ignore CaseClassField accessed"));
    }

    boolean hasDefaultValue()
    {
        return hasDefaultValue;
    }

    boolean isMutable()
    {
        return mutable;
    }

    @Override
    public String toString()
    {
        return "CaseClassItem{" +
            "name=" + name +
            ", type=" + type +
            ", erasedType=" + erasedType +
            ", hasDefaultValue=" + hasDefaultValue +
            ", mutable=" + mutable +
            '}';
    }
}
