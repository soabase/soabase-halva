// Auto generated from com.company.AliasExample by Soabase TypeAlias annotation processor
package com.company;

import io.soabase.halva.alias.TypeAliasType;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.suagar.ConsList;
import io.soabase.halva.tuple.details.Pair;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface AliasExampleAlias extends ConsList<Pair<String, Integer>> {
    TypeAliasType<ConsList<Pair<String, Integer>>, AliasExampleAlias> TypeAliasType = new TypeAliasType<>(new AnyType<ConsList<Pair<String, Integer>>>(){}, new AnyType<AliasExampleAlias>(){}, AliasExampleAlias::AliasExampleAlias);

    static AliasExampleAlias AliasExampleAlias(ConsList<Pair<String, Integer>> instance) {
        return new AliasExampleAlias() {
            @Override
            public String toString() {
                return instance.toString();
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
            public Iterator<Pair<String, Integer>> iterator() {
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
            public boolean add(Pair<String, Integer> arg0) {
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
            public boolean addAll(Collection<? extends Pair<String, Integer>> arg0) {
                return instance.addAll(arg0);
            }

            @Override
            public boolean addAll(int arg0, Collection<? extends Pair<String, Integer>> arg1) {
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
            public Pair<String, Integer> get(int arg0) {
                return instance.get(arg0);
            }

            @Override
            public Pair<String, Integer> set(int arg0, Pair<String, Integer> arg1) {
                return instance.set(arg0, arg1);
            }

            @Override
            public void add(int arg0, Pair<String, Integer> arg1) {
                instance.add(arg0, arg1);
            }

            @Override
            public Pair<String, Integer> remove(int arg0) {
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
            public ListIterator<Pair<String, Integer>> listIterator() {
                return instance.listIterator();
            }

            @Override
            public ListIterator<Pair<String, Integer>> listIterator(int arg0) {
                return instance.listIterator(arg0);
            }

            @Override
            public List<Pair<String, Integer>> subList(int arg0, int arg1) {
                return instance.subList(arg0, arg1);
            }

            @Override
            public Pair<String, Integer> head() {
                return instance.head();
            }

            @Override
            public ConsList<Pair<String, Integer>> tail() {
                return instance.tail();
            }

            @Override
            public ConsList<Pair<String, Integer>> concat(ConsList<Pair<String, Integer>> arg0) {
                return instance.concat(arg0);
            }

            @Override
            public ConsList<Pair<String, Integer>> cons(Pair<String, Integer> arg0) {
                return instance.cons(arg0);
            }
        };
    }
}
