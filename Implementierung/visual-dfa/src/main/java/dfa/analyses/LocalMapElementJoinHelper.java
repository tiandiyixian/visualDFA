package dfa.analyses;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import soot.jimple.internal.JimpleLocal;

/**
 * A {@code LocalMapElementJoinHelper} helps performing joins when provided with a method to join single values.
 *
 * @param <V>
 *        the type of values
 * @param <E>
 *        the type of {@code LocalMapElement}
 * 
 * @author Sebastian Rauch
 */
public abstract class LocalMapElementJoinHelper<V, E extends LocalMapElement<V>> {

    /**
     * Performs a join operation on {@code elements}.
     * 
     * @param elements
     *        the elements to join
     * @return the result of the join
     */
    public E performJoin(Set<E> elements) {
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("there must be at least one element to join");
        }

        if (!doLocalsMatch(elements)) {
            throw new IllegalArgumentException("loclas not matching");
        }

        Iterator<E> it = elements.iterator();
        E refElement = it.next();
        Map<JimpleLocal, V> refMap = refElement.getLocalMap();
        Set<Entry<JimpleLocal, V>> entries = refMap.entrySet();

        @SuppressWarnings("unchecked")
        E result = (E) refElement.clone(); // assume clone() returns the same type
        for (Entry<JimpleLocal, V> entry : entries) {
            JimpleLocal local = entry.getKey();
            V localJoinResult = doValueJoin(elements, local);
            result.setValue(local, localJoinResult);
        }

        return result;
    }

    /**
     * Performs a join only on the {@code Value}s mapped to the given {@code JimpleLocal}.
     * 
     * @param elements
     *        the {@code Set} of elements to join, must not be empty or {@code null}
     * @param local
     *        the {@code JimpleLocal} for which the {@code Value}s should be joined
     * @return the result of the join
     */
    public abstract V doValueJoin(Set<E> elements, JimpleLocal local);

    private boolean doLocalsMatch(Set<E> elements) {
        Iterator<? extends LocalMapElement<V>> it = elements.iterator();
        LocalMapElement<V> refElement = it.next();
        Map<JimpleLocal, V> refMap = refElement.getLocalMap();
        while (it.hasNext()) {
            LocalMapElement<V> compElement = it.next();
            Map<JimpleLocal, V> compMap = compElement.getLocalMap();

            for (Map.Entry<JimpleLocal, V> entry : refMap.entrySet()) {
                if (!compMap.containsKey(entry.getKey())) {
                    return false;
                }
            }

            for (Map.Entry<JimpleLocal, V> entry : compMap.entrySet()) {
                if (!refMap.containsKey(entry.getKey())) {
                    return false;
                }
            }
        }

        return true;
    }

}
