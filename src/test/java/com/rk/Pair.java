package com.rk;

public class Pair<T, V> {
    private T left;
    private V right;

    public Pair(T left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <T, V> Pair<T, V> of(T left, V right) {
        return new Pair<>(left, right);
    }

    public T getLeft() {
        return left;
    }

    public V getRight() {
        return right;
    }
}
