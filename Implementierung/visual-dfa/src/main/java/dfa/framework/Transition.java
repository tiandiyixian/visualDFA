package dfa.framework;

import soot.Unit;

/**
 * A {@code Transition} executes a transition of a given {@code LatticeElement} on a given {@code Unit}.
 *
 * @param <E>
 *        the type of {@code LatticeElement} to perform transitions on
 * 
 * @author Sebastian Rauch
 */
public interface Transition<E extends LatticeElement> {

    /**
     * Calculates the transition of {@code element} on {@code unit}.
     * 
     * @param element
     *        the {@code LatticeElement} used in the transition
     * @param unit
     *        the {@code Unit} that determines how exactly the transition works
     * @return the result of the transition
     */
    E transition(E element, Unit unit);

}
