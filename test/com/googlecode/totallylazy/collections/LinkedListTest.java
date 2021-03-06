package com.googlecode.totallylazy.collections;

import com.googlecode.totallylazy.predicates.Predicates;
import com.googlecode.totallylazy.functions.TimeReport;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.option;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.functions.Count.count;
import static com.googlecode.totallylazy.collections.PersistentList.constructors.empty;
import static com.googlecode.totallylazy.collections.PersistentList.constructors.list;
import static com.googlecode.totallylazy.collections.PersistentSortedSet.constructors.sortedSet;
import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.totallylazy.numbers.Numbers.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

public class LinkedListTest {
    @Test
    public void canFold() throws Exception {
        assertThat(list("Dan", "Matt").fold(0, count()).intValue(), is(2));
    }

    @Test
    public void canLookupIndexOf() throws Exception {
        assertThat(list("Dan", "Matt").indexOf("Dan"), is(0));
        assertThat(list("Dan", "Matt").indexOf("Matt"), is(1));
    }

    @Test
    public void canLookupLastIndexOf() throws Exception {
        assertThat(list("Dan", "Matt", "Dan").lastIndexOf("Dan"), is(2));
        assertThat(list("Dan", "Matt", "Dan").lastIndexOf("Matt"), is(1));
        assertThat(list("Dan", "Matt", "Dan").lastIndexOf("Chris"), is(-1));
        assertThat(list().lastIndexOf("Chris"), is(-1));
    }

    @Test
    public void supportsSubList() throws Exception {
        PersistentList<String> all = list("Dan", "Matt", "Chris", "Tom");
        assertThat(all.subList(2, 4), is(list("Chris", "Tom")));
        assertThat(all.subList(2, 3), is(list("Chris")));
        assertThat(all.subList(2, 2), is(PersistentList.constructors.<String>list()));
        assertThat(PersistentList.constructors.<String>list().subList(0, 0), is(PersistentList.constructors.<String>list()));

        try {
            all.subList(2, 10);
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @Test
    public void canLookupByIndex() throws Exception {
        assertThat(list("Dan", "Matt").get(0), is("Dan"));
        assertThat(list("Dan", "Matt").get(1), is("Matt"));
    }

    @Test
    public void supportsOneElement() throws Exception {
        assertThat(list(1), hasExactly(1));
    }

    @Test
    public void supportsTwoElements() throws Exception {
        assertThat(list(1, 2), hasExactly(1, 2));
    }

    @Test
    public void supportsThreeElements() throws Exception {
        assertThat(list(1, 2, 3), hasExactly(1, 2, 3));
    }

    @Test
    public void supportsFourElements() throws Exception {
        assertThat(list(1, 2, 3, 4), hasExactly(1, 2, 3, 4));
    }

    @Test
    public void supportsFiveElements() throws Exception {
        assertThat(list(1, 2, 3, 4, 5), hasExactly(1, 2, 3, 4, 5));
    }

    @Test
    public void supportsVarArgsForMoreThanFive() throws Exception {
        assertThat(list(1, 2, 3, 4, 5, 6), hasExactly(1, 2, 3, 4, 5, 6));
    }

    @Test
    public void supportsRemove() throws Exception {
        assertThat(list(1, 2, 3, 4, 5, 6).delete(3), hasExactly(1, 2, 4, 5, 6));
        assertThat(list(1, 2, 3, 4, 5, 6).delete(6), hasExactly(1, 2, 3, 4, 5));
    }

    @Test
    @Ignore
    public void removeIsPrettyFast() throws Exception {
        final PersistentList<Number> range = range(1, 1000).toPersistentList();
        TimeReport report = TimeReport.time(1000000, () -> {
            return range.delete(3);
        });
        System.out.println(report);
    }

    @Test
    public void supportsEquality() throws Exception {
        assertThat(list(1, 2, 3, 4, 5, 6), is(list(1, 2, 3, 4, 5, 6)));
        assertThat(list(1, 2, 3, 4, 5, 6), not(list(1, 2, 3, 4, 5)));
    }

    @Test
    public void supportsSize() throws Exception {
        assertThat(list(1, 2, 3, 4, 5, 6).size(), is(6));
        assertThat(list(1, 2, 3, 4, 5).size(), is(5));
    }

    @Test
    public void supportsAdd() throws Exception {
        assertThat(list(1).append(2), hasExactly(1, 2));
    }

    @Test
    public void supportsCons() throws Exception {
        assertThat(list(1).cons(2), hasExactly(2, 1));
    }

    @Test
    public void supportsToList() throws Exception {
        final List<Integer> actual = list(1).toMutableList();
        final List<Integer> expected = new ArrayList<Integer>() {{
            add(1);
        }};
        assertThat(actual, is(expected));
    }

    @Test
    public void supportIterator() throws Exception {
        final Iterator<Integer> iterator = list(1, 2, 3).iterator();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(1));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(2));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(3));
        assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void canJoin() throws Exception {
        PersistentList<Integer> join = list(1, 2, 3, 4).joinTo(list(4, 3, 2, 1));
        assertThat(join, hasExactly(1, 2, 3, 4, 4, 3, 2, 1));
        PersistentSortedSet<Integer> sortedSet = list(2, 1, 4, 3).joinTo(sortedSet(3, 4));
        assertThat(sortedSet, hasExactly(1, 2, 3, 4));
    }

    @Test
    public void supportsHeadOption() {
        assertThat(list(1, 2, 3).headOption(), is(some(1)));
        assertThat(empty(Integer.class).headOption(), is(none(Integer.class)));
    }

}
