/*
 * @(#)$Id$
 *
 * Copyright 2006-2008 Makoto YUI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Makoto YUI - initial implementation
 */
package com.codebutler.tunestreamer.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 *
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class SortedArrayList<E> extends ArrayList<E> implements SortedList<E> {
    private static final long serialVersionUID = -5407829713506240290L;

    private Comparator<E> cmp;
    private boolean duplicateAllowed = true;

    public SortedArrayList() {
        this(new DefaultComparator<E>());
    }

    public SortedArrayList(Comparator<E> cmp) {
        super();
        this.cmp = cmp;
    }

    @SuppressWarnings("unchecked")
    public SortedArrayList(int initialCapacity, boolean comparable) {
        this(initialCapacity, comparable ? new ComparableComparator() : new DefaultComparator<E>());
    }

    public SortedArrayList(int initialCapacity, Comparator<E> cmp) {
        super(initialCapacity);
        this.cmp = cmp;
    }

    public boolean isDuplicateAllowed() {
        return duplicateAllowed;
    }

    public void allowDuplicate(boolean allow) {
        this.duplicateAllowed = allow;
    }

    public void setComparator(Comparator<E> cmp) {
        this.cmp = cmp;
    }

    @Override
    public boolean add(E v) {
        int insertionIdx = Collections.binarySearch(this, v, cmp);
        if(insertionIdx < 0) {// duplicate not found
            insertionIdx = (-insertionIdx) - 1;
        } else if(!duplicateAllowed) {// has duplicate
            return false;
        }
        super.add(insertionIdx, v);
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for(E e : c) {
            if(!add(e)) {
                return false;
            }
        }
        return true;
    }

    public E get(E probe) {
        final int idx = Collections.binarySearch(this, probe, cmp);
        if(idx >= 0) {
            return get(idx);
        }
        return null;
    }

    /**
     * An efficient version of {@link #contains(Object)}
     */
    public boolean contains2(E o) {
        return indexOf2(o) >= 0;
    }

    /**
     * An efficient version of {@link #indexOf(Object)}
     */
    public int indexOf2(E o) {
        final int idx = Collections.binarySearch(this, o, cmp);
        return idx < 0 ? -1 : idx;
    }

    // ------------------------------------------
    // Unsupported Operations

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

}