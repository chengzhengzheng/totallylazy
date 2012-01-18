package com.googlecode.totallylazy;

import java.util.NoSuchElementException;

import static com.googlecode.totallylazy.Callers.call;

public abstract class Either<L, R> implements Functor<R, Either<L, ?>>{
    public static <L,R> Either<L,R> right(R value) {
        return new Right<L,R>(value);
    }

    public static <L,R> Either<L,R> left(L value) {
        return new Left<L,R>(value);
    }

    public boolean isRight() {
        return false;
    }

    public boolean isLeft() {
        return false;
    }

    public R right(){
        throw new NoSuchElementException();
    }

    public L left(){
        throw new NoSuchElementException();
    }

    public <S> S fold(final S seed, final Callable2<? super S, ? super L, ? extends S> left, final Callable2<? super S, ? super R, ? extends S> right) {
        return isLeft() ? call(left, seed, left()) : call(right, seed, right());
    }

    public <S> S map(final Callable1<? super L, S> left, final Callable1<? super R, ? extends S> right) {
        return isLeft() ? call(left, left()) : call(right, right());
    }

    @Override
    public <S> Either<L, S> map(Callable1<? super R, ? extends S> callable) {
        return isLeft() ? Either.<L, S>left(left()) : Either.<L, S>right(call(callable, right()));
    }

    public abstract Object value();
}
