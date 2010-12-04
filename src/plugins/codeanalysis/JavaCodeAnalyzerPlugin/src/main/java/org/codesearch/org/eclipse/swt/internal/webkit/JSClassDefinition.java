/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others. All rights reserved.
 * The contents of this file are made available under the terms
 * of the GNU Lesser General Public License (LGPL) Version 2.1 that
 * accompanies this distribution (lgpl-v21.txt).  The LGPL is also
 * available at http://www.gnu.org/licenses/lgpl.html.  If the version
 * of the LGPL at http://www.gnu.org is different to the version of
 * the LGPL accompanying this distribution and there is any conflict
 * between the two license versions, the terms of the LGPL accompanying
 * this distribution shall govern.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.codesearch.org.eclipse.swt.internal.webkit;


public class JSClassDefinition {
    public int version;
    /** @field cast=(JSClassAttributes) */
    public int attributes;
    /** @field cast=(const char*) */
    public long /*int*/ className;
    /** @field cast=(JSClassRef) */
    public long /*int*/ parentClass;
    /** @field cast=(const JSStaticValue*) */
    public long /*int*/ staticValues;
    /** @field cast=(const JSStaticFunction*) */
    public long /*int*/ staticFunctions;
    /** @field cast=(JSObjectInitializeCallback) */
    public long /*int*/ initialize;
    /** @field cast=(JSObjectFinalizeCallback) */
    public long /*int*/ finalize;
    /** @field cast=(JSObjectHasPropertyCallback) */
    public long /*int*/ hasProperty;
    /** @field cast=(JSObjectGetPropertyCallback) */
    public long /*int*/ getProperty;
    /** @field cast=(JSObjectSetPropertyCallback) */
    public long /*int*/ setProperty;
    /** @field cast=(JSObjectDeletePropertyCallback) */
    public long /*int*/ deleteProperty;
    /** @field cast=(JSObjectGetPropertyNamesCallback) */
    public long /*int*/ getPropertyNames;
    /** @field cast=(JSObjectCallAsFunctionCallback) */
    public long /*int*/ callAsFunction;
    /** @field cast=(JSObjectCallAsConstructorCallback) */
    public long /*int*/ callAsConstructor;
    /** @field cast=(JSObjectHasInstanceCallback) */
    public long /*int*/ hasInstance;
    /** @field cast=(JSObjectConvertToTypeCallback) */
    public long /*int*/ convertToType;
    
    public static final int sizeof = WebKitGTK.JSClassDefinition_sizeof();
}
