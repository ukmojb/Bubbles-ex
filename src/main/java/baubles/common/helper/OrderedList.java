package baubles.common.helper;

import java.util.ArrayList;

public class OrderedList<T extends Comparable<T>> extends ArrayList<T> {

    @Override
    public boolean add(T t) {
        int i = 0;
        for (; i < this.size(); i++) {
            T t2 = this.get(i);
            if (t == t2) return false; // TODO Maybe throw
            if (t.compareTo(t2) <= 0) {
                super.add(i, t);
                return true;
            }
        }
        return super.add(t);
    }
}
