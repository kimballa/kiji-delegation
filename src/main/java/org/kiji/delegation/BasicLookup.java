/**
 * (c) Copyright 2012 WibiData, Inc.
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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
 */

package org.kiji.delegation;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Basic provider of the {@link Lookup} API. This Lookup instance returns "some"
 * provider of a specified interface, without a specified means of preferring
 * one implementation over another.
 *
 * @param <T> the type that this Lookup instance provides.
 */
public final class BasicLookup<T> extends Lookup<T> {
  /** The ClassLoader that we use to look up service implementations. */
  private final ClassLoader mClassLoader;
  /** A representation of the interface or abstract class the user wants to load. */
  private final Class<T> mClass;
  /** The actual ServiceLoader, which does the heavy lifting and wraps the classloader. */
  private final ServiceLoader<T> mServiceLoader;

  /**
   * Create a BasicLookup instance. Package-private c'tor; clients should use the
   * Lookup.get() factory methods.
   *
   * @param clazz the abstract class or interface to lookup a provider for.
   * @param classLoader the classloader to use.
   */
  BasicLookup(Class<T> clazz, ClassLoader classLoader) {
    assert null != clazz;
    assert null != classLoader;
    mClass = clazz;
    mClassLoader = classLoader;
    mServiceLoader = ServiceLoader.load(clazz, mClassLoader);
  }

  /**
   * Lookup a provider instance for the specified clazz and return the first
   * instance we can create.
   *
   * @return the first provider instance for clazz we discover.
   * @throws NoSuchProviderException if no runtime provider for the interface
   *     can be found.
   */
  public T lookup() {
    Iterator<T> it = mServiceLoader.iterator();
    if (!it.hasNext()) {
      throw new NoSuchProviderException(mClass);
    }

    // Return the first service instance we can find.
    return it.next();
  }

  /**
   * @return an iterator of all provider instances for the specified class in no
   *     particular order.
   */
  public Iterator<T> iterator() {
    return mServiceLoader.iterator();
  }
}
