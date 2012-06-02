/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.2
 */
public class SetContainer<E> extends Container<E>
{
  private final Class<E> componentType;

  private final Set<E> set;

  private E[] array;

  public SetContainer(Class<E> componentType)
  {
    this(componentType, new HashSet<E>());
  }

  public SetContainer(Class<E> componentType, Set<E> set)
  {
    this.componentType = componentType;
    this.set = set;
  }

  public final Class<E> getComponentType()
  {
    return componentType;
  }

  @Override
  public synchronized boolean isEmpty()
  {
    checkActive();
    return set.isEmpty();
  }

  public synchronized E[] getElements()
  {
    checkActive();
    if (array == null)
    {
      @SuppressWarnings("unchecked")
      E[] a = (E[])Array.newInstance(componentType, set.size());
      array = set.toArray(a);
    }

    return array;
  }

  public boolean addElement(E element)
  {
    checkActive();
    boolean added;
    synchronized (this)
    {
      added = set.add(element);
      if (added)
      {
        array = null;
      }

      notifyAll();
    }

    if (added)
    {
      fireElementAddedEvent(element);
    }

    return added;
  }

  public boolean removeElement(E element)
  {
    checkActive();
    boolean removed;
    synchronized (this)
    {
      removed = set.remove(element);
      if (removed)
      {
        array = null;
      }

      notifyAll();
    }

    if (removed)
    {
      fireElementRemovedEvent(element);
    }

    return removed;
  }

  protected Set<E> getSet()
  {
    return set;
  }
}
