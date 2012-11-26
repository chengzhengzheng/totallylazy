package com.googlecode.totallylazy.collections;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.First;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Segment;
import com.googlecode.totallylazy.predicates.LogicalPredicate;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.onlyOnce;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.filter;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.collections.ImmutableList.constructors.list;
import static com.googlecode.totallylazy.collections.ImmutableList.constructors.reverse;

public class ListMap<K, V> implements ImmutableMap<K, V> {
    private ImmutableList<Pair<K, V>> list;

    private ListMap(ImmutableList<Pair<K, V>> list) {
        this.list = list;
    }

    public static <K, V> ImmutableMap<K, V> emptyListMap(Class<K> kClass, Class<V> vClass) {
        return emptyListMap();
    }

    public static <K, V> ImmutableMap<K, V> emptyListMap() {
        return listMap(ImmutableList.constructors.<Pair<K,V>>empty());
    }

    public static <K, V> ImmutableMap<K, V> listMap(K key, V value) {
        return listMap(pair(key, value));
    }

    public static <K, V> ImmutableMap<K, V> listMap(K key1, V value1, K key2, V value2) {
        return listMap(sequence(pair(key1, value1), pair(key2, value2)));
    }

    public static <K, V> ImmutableMap<K, V> listMap(K key1, V value1, K key2, V value2, K key3, V value3) {
        return listMap(sequence(pair(key1, value1), pair(key2, value2), pair(key3, value3)));
    }

    public static <K, V> ImmutableMap<K, V> listMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
        return listMap(sequence(pair(key1, value1), pair(key2, value2), pair(key3, value3), pair(key4, value4)));
    }

    public static <K, V> ImmutableMap<K, V> listMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
        return listMap(sequence(pair(key1, value1), pair(key2, value2), pair(key3, value3), pair(key4, value4), pair(key5, value5)));
    }

    public static <K, V> ImmutableMap<K, V> listMap(Iterable<? extends Pair<K, V>> pairs) {
        return listMap(reverse(sequence((pairs))));
    }

    public static <K, V> ImmutableMap<K, V> listMap(Pair<K, V> pair) {
        return listMap(list(pair));
    }

    public static <K, V> ImmutableMap<K, V> listMap(ImmutableList<Pair<K, V>> list1) {
        return new ListMap<K, V>(list1);
    }

    @Override
    public ImmutableMap<K, V> cons(Pair<K, V> head) {
        return contains(head.first()) ? listMap(list.map(replace(head))) : listMap(list.cons(head));
    }

    private Callable1<Pair<K, V>, Pair<K, V>> replace(final Pair<K, V> newValue) {
        return new Callable1<Pair<K, V>, Pair<K, V>>() {
            @Override
            public Pair<K, V> call(Pair<K, V> oldValue) throws Exception {
                return oldValue.first().equals(newValue.first()) ? newValue : oldValue;
            }
        };
    }

    @Override
    public <C extends Segment<Pair<K, V>>> C joinTo(C rest) {
        return list.joinTo(rest);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Pair<K, V> head() throws NoSuchElementException {
        return list.head();
    }

    @Override
    public ImmutableMap<K, V> tail() throws NoSuchElementException {
        return listMap(list.tail());
    }

    @Override
    public Option<V> get(K key) {
        return find(is(key));
    }

    @Override
    public Option<V> find(Predicate<? super K> predicate) {
        return list.find(key(predicate)).map(Callables.<V>second());
    }

    @Override
    public ImmutableMap<K, V> put(K key, V value) {
        return cons(Pair.pair(key, value));
    }

    @Override
    public ImmutableMap<K, V> remove(K key) {
        return filterKeys(is(not(key)));
    }

    @Override
    public ImmutableMap<K, V> filterKeys(Predicate<? super K> predicate) {
        return listMap(list.filter(key(predicate)));
    }

    @Override
    public ImmutableMap<K, V> filterValues(Predicate<? super V> predicate) {
        return listMap(list.filter(where(Callables.<V>second(), predicate)));
    }

    @Override
    public <NewV> ImmutableMap<K, NewV> map(Callable1<? super V, ? extends NewV> transformer) {
        return listMap(list.map(Callables.<K, V, NewV>second(transformer)));
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public ImmutableList<Pair<K, V>> immutableList() {
        return reverse(list);
    }

    @Override
    public Map<K, V> toMap() {
        return ImmutableMap.methods.toMap(this);
    }

    @Override
    public boolean contains(K other) {
        return list.exists(key(other));
    }

    @Override
    public boolean exists(Predicate<? super K> predicate) {
        return list.exists(key(predicate));
    }

    @Override
    public Iterator<Pair<K, V>> iterator() {
        return immutableList().iterator();
    }

    private Predicate<First<K>> key(Predicate<? super K> predicate) {
        return where(Callables.<K>first(), predicate);
    }

    private Predicate<First<K>> key(K key) {
        return key(is(key));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ListMap && ((ListMap) obj).list.equals(list);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public String toString() {
        return immutableList().toString();
    }

    @Override
    public <S> S fold(S seed, Callable2<? super S, ? super Pair<K, V>, ? extends S> callable) {
        return list.fold(seed, callable);
    }
}
