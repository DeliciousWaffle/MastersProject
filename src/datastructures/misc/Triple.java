package datastructures.misc;

/**
 * Super simple class that holds a triplet of whatever.
 * @param <A> generic type
 * @param <B> generic type
 * @param <C> generic type
 */
public class Triple<A, B, C> extends Pair<A, B> {

    private C third;

    public Triple(A first, B second, C third) {
        super(first, second);
        this.third = third;
    }

    public C getThird() {
        return third;
    }

    public void setThird(C third) {
        this.third = third;
    }

    @Override
    public String toString() {
        return "<" + super.getFirst() + ", " + super.getSecond() + ", " + third + ">";
    }
}