package Misc;

public final class Triple<A, B, C> {

    A first;
    B second;
    C third;

    public Triple() {
        first = null;
        second = null;
        third = null;
    }

    public Triple(A f, B s, C t) {
        first = f;
        second = s;
        third = t;
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public C getThird() {
        return third;
    }

    public void setThird(C third) {
        this.third = third;
    }
}
