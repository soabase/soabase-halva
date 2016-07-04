// Auto generated from com.company.ExampleContainer_ by Soabase io.soabase.halva.container.TypeContainer annotation processor
package com.company;

import io.soabase.halva.alias.TypeAliasType;
import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyClassTuple;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.any.AnyVal;
import io.soabase.halva.sugar.ConsList;
import io.soabase.halva.tuple.ClassTuplable;
import io.soabase.halva.tuple.Tuplable;
import io.soabase.halva.tuple.Tuple;
import io.soabase.halva.tuple.details.Tuple2;
import java.io.Serializable;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Generated;

@Generated("io.soabase.halva.container.TypeContainer")
public class ExampleContainer {
    public static class MyStack implements Serializable, ExampleContainer_.MyStack, Tuplable, ClassTuplable {
        private static final Class classTuplableClass = MyStackMatch(Any.any(), Any.any()).getClass();

        private final Stack stack;

        private final int value;

        protected MyStack(Stack stack, int value) {
            if ( stack == null ) {
                throw new IllegalArgumentException("\"stack\" does not have a default value");
            }
            this.stack = stack;
            this.value = value;
        }

        @Override
        public Stack stack() {
            return stack;
        }

        @Override
        public int value() {
            return value;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder copy() {
            return new Builder(this);
        }

        public static MyStack MyStack(Stack stack, int value) {
            return new MyStack(stack, value);
        }

        public static AnyClassTuple<MyStack> MyStackMatch(AnyVal<? extends Stack> stack, AnyVal<? extends Integer> value) {
            return new AnyClassTuple<MyStack>(Tuple.Tu(Any.loose(stack), Any.loose(value))){};
        }

        @Override
        public Class getClassTuplableClass() {
            return classTuplableClass;
        }

        @Override
        public boolean equals(Object rhsObj) {
            if ( this == rhsObj ) {
                return true;
            }
            if ( rhsObj == null || getClass() != rhsObj.getClass() ) {
                return false;
            }
            MyStack rhs = (MyStack)rhsObj;
            if ( !stack.equals(rhs.stack) ) {
                return false;
            }
            if ( value != rhs.value ) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = stack.hashCode();
            result = 31 * result + value;
            return result;
        }

        @Override
        public Tuple2<Stack, Integer> tuple() {
            return Tuple.Tu(stack(), value());
        }

        public String debugString() {
            return "MyStack { " +
                "stack=" + stack + "; " +
                "value=" + value + "; " +
            '}';
        }

        @Override
        public String toString() {
            return "MyStack(" +
            stack +
            ", " + value +
            ')';
        }

        public static final class Builder {
            private Stack stack;

            private int value;

            private Builder() {
            }

            private Builder(MyStack rhs) {
                stack = rhs.stack;
                value = rhs.value;
            }

            public MyStack build() {
                return new MyStack(
                    stack, 
                    value
                );
            }

            public Builder stack(Stack stack) {
                this.stack = stack;
                return this;
            }

            public Builder value(int value) {
                this.value = value;
                return this;
            }
        }
    }

    public interface Stack extends ConsList<List<String>> {
        TypeAliasType<ConsList<List<String>>, Stack> TypeAliasType = new TypeAliasType<>(new AnyType<ConsList<List<String>>>(){}, new AnyType<Stack>(){}, Stack::Stack);

        static Stack Stack(ConsList<List<String>> instance) {
            return new Stack() {
                @Override
                public String toString() {
                    return instance.toString();
                }

                @Override
                public List<String> head() {
                    return instance.head();
                }

                @Override
                public Stack tail() {
                    return Stack(instance.tail());
                }

                @Override
                public Stack concat(ConsList<List<String>> arg0) {
                    return Stack(instance.concat(arg0));
                }

                @Override
                public Stack cons(List<String> arg0) {
                    return Stack(instance.cons(arg0));
                }

                @Override
                public int size() {
                    return instance.size();
                }

                @Override
                public boolean isEmpty() {
                    return instance.isEmpty();
                }

                @Override
                public boolean contains(Object arg0) {
                    return instance.contains(arg0);
                }

                @Override
                public Iterator<List<String>> iterator() {
                    return instance.iterator();
                }

                @Override
                public Object[] toArray() {
                    return instance.toArray();
                }

                @Override
                public <T> T[] toArray(T[] arg0) {
                    return instance.toArray(arg0);
                }

                @Override
                public boolean add(List<String> arg0) {
                    return instance.add(arg0);
                }

                @Override
                public boolean remove(Object arg0) {
                    return instance.remove(arg0);
                }

                @Override
                public boolean containsAll(Collection<?> arg0) {
                    return instance.containsAll(arg0);
                }

                @Override
                public boolean addAll(Collection<? extends List<String>> arg0) {
                    return instance.addAll(arg0);
                }

                @Override
                public boolean addAll(int arg0, Collection<? extends List<String>> arg1) {
                    return instance.addAll(arg0, arg1);
                }

                @Override
                public boolean removeAll(Collection<?> arg0) {
                    return instance.removeAll(arg0);
                }

                @Override
                public boolean retainAll(Collection<?> arg0) {
                    return instance.retainAll(arg0);
                }

                @Override
                public void clear() {
                    instance.clear();
                }

                @Override
                public boolean equals(Object arg0) {
                    return (this == arg0) || instance.equals(arg0);
                }

                @Override
                public int hashCode() {
                    return instance.hashCode();
                }

                @Override
                public List<String> get(int arg0) {
                    return instance.get(arg0);
                }

                @Override
                public List<String> set(int arg0, List<String> arg1) {
                    return instance.set(arg0, arg1);
                }

                @Override
                public void add(int arg0, List<String> arg1) {
                    instance.add(arg0, arg1);
                }

                @Override
                public List<String> remove(int arg0) {
                    return instance.remove(arg0);
                }

                @Override
                public int indexOf(Object arg0) {
                    return instance.indexOf(arg0);
                }

                @Override
                public int lastIndexOf(Object arg0) {
                    return instance.lastIndexOf(arg0);
                }

                @Override
                public ListIterator<List<String>> listIterator() {
                    return instance.listIterator();
                }

                @Override
                public ListIterator<List<String>> listIterator(int arg0) {
                    return instance.listIterator(arg0);
                }

                @Override
                public List<List<String>> subList(int arg0, int arg1) {
                    return instance.subList(arg0, arg1);
                }
            };
        }
    }
}
