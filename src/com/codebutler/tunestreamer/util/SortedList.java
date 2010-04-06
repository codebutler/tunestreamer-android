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

import java.util.Comparator;
import java.util.List;

/**
 *
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 *
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public interface SortedList<E> extends List<E>/* ,Iterable<E> */{

    public boolean isDuplicateAllowed();

    public void allowDuplicate(boolean allow);

    public void setComparator(Comparator<E> cmp);

    //public boolean add(E v);
    //public boolean remove(Object v);
    //public E get(int i);
    //public int size();

    public E get(E probe);

    /**
     * An efficient version of {@link #contains(Object)}
     */
    public boolean contains2(E o);

    /**
     * An efficient version of {@link #indexOf(Object)}
     */
    public int indexOf2(E o);

}