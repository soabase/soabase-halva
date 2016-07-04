package io.soabase.halva.any;

import io.soabase.com.google.inject.internal.MoreTypes;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static io.soabase.com.google.inject.internal.MoreTypes.canonicalize;

class InternalType
{
    final Type type;
    final Class<?> rawType;

    InternalType(Type type)
    {
        this.type = type;
        rawType = MoreTypes.getRawType(type);
    }

    @SuppressWarnings("unchecked")
    static InternalType getInternalType(Class<?> superType, boolean throwIfMisspecified)
    {
        Type superclass = superType.getGenericSuperclass();
        if ( superclass instanceof Class )
        {
            if ( throwIfMisspecified )
            {
                throw new RuntimeException("Missing type parameter");
            }
            return new InternalType(superType);
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        Type type = canonicalize(parameterized.getActualTypeArguments()[0]);
        if ( !MoreTypes.isFullySpecified(type) )
        {
            if ( throwIfMisspecified )
            {
                throw new RuntimeException("Parameterized type is not fully specified");
            }
            return new InternalType(superType);
        }
        return new InternalType(type);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    boolean isAssignableFrom(InternalType from)
    {
        //noinspection LoopStatementThatDoesntLoop
        do
        {
            if ( (type == null) && (from.type != null) )
            {
                break;
            }

            if ( (type != null) && (from.type == null) )
            {
                break;
            }

            if ( (type instanceof Class) != (from.type instanceof Class) )
            {
                return type.equals(from.type);
            }

            return ((Class)type).isAssignableFrom((Class)from.type);
        } while ( false );
        return false;
    }
}
