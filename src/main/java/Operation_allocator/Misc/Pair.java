package Operation_allocator.Misc;

public final class Pair<T, S> {

    private T first;
    private S second;

    public Pair() {
        first = null;
        second = null;
    }

    public Pair(T f, S s) {
        first = f;
        second = s;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }
}
