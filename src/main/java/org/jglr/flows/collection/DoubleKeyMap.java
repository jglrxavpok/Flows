package org.jglr.flows.collection;

import java.util.HashMap;

public class DoubleKeyMap<K1, K2, V> extends HashMap<DoubleKeyMap.DoubleKey<K1, K2>, V> {

    public V put(K1 key1, K2 key2, V value) {
        return put(new DoubleKey<>(key1, key2), value);
    }

    public V get(K1 key1, K2 key2) {
        return get(new DoubleKey<>(key1, key2));
    }

    public boolean containsKey(K1 key1, K2 key2) {
        return containsKey(new DoubleKey<>(key1, key2));
    }

    protected static class DoubleKey<Ka, Kb> {

        private final Ka keyA;
        private final Kb keyB;

        public DoubleKey(Ka keyA, Kb keyB) {
            this.keyA = keyA;
            this.keyB = keyB;
        }

        @Override
        public int hashCode() {
            return keyA.hashCode() + keyB.hashCode() << 24;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof DoubleKey) {
                DoubleKey other = ((DoubleKey) obj);
                return keyA.equals(other.keyA) && keyB.equals(other.keyB);
            }
            return super.equals(obj);
        }
    }
}
