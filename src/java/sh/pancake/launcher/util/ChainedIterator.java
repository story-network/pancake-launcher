/*
 * Created on Sun Feb 07 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.launcher.util;

import java.util.Iterator;

public class ChainedIterator<T> implements Iterator<T> {

    private Iterator<Iterator<T>> iterateIterator;
    private Iterator<T> last;

    public ChainedIterator(Iterable<Iterator<T>> iterable) {
        this.iterateIterator = iterable.iterator();
    
        if (iterateIterator.hasNext()) {
            this.last = iterateIterator.next();
        }
    }

    @Override
    public boolean hasNext() {
        return last != null && (last.hasNext() || iterateIterator.hasNext());
    }

    @Override
    public T next() {
        if (last == null) return null;

        while (!last.hasNext()) {
            if (!iterateIterator.hasNext()) return null;

            this.last = iterateIterator.next();
        }

        return last.next();
    }
    
}
