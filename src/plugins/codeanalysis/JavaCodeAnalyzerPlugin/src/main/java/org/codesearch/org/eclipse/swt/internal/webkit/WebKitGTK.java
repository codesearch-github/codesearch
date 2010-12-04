/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others. All rights reserved.
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


import org.codesearch.org.eclipse.swt.internal.C;

public class WebKitGTK extends C {

	/** Constants */
	public static final int kJSTypeUndefined = 0;
	public static final int kJSTypeNull = 1;
	public static final int kJSTypeBoolean = 2;
	public static final int kJSTypeNumber = 3;
	public static final int kJSTypeString = 4;
	public static final int kJSTypeObject = 5;
	public static final int SOUP_MEMORY_TAKE = 1;
	public static final int WEBKIT_DOWNLOAD_STATUS_ERROR = -1;
	public static final int WEBKIT_DOWNLOAD_STATUS_CANCELLED = 2;
	public static final int WEBKIT_DOWNLOAD_STATUS_FINISHED = 3;
	public static final int WEBKIT_LOAD_COMMITTED = 1;
	public static final int WEBKIT_LOAD_FINISHED = 2;

	/** Signals */
	public static final byte[] authenticate = ascii ("authenticate"); // $NON-NLS-1$
	public static final byte[] close_web_view = ascii ("close-web-view"); // $NON-NLS-1$
	public static final byte[] console_message = ascii ("console-message"); // $NON-NLS-1$
	public static final byte[] create_web_view = ascii ("create-web-view"); // $NON-NLS-1$
	public static final byte[] download_requested = ascii ("download-requested"); // $NON-NLS-1$
	public static final byte[] hovering_over_link = ascii ("hovering-over-link"); // $NON-NLS-1$
	public static final byte[] mime_type_policy_decision_requested = ascii ("mime-type-policy-decision-requested"); // $NON-NLS-1$
	public static final byte[] navigation_policy_decision_requested = ascii ("navigation-policy-decision-requested"); // $NON-NLS-1$
	public static final byte[] notify_load_status = ascii ("notify::load-status"); // $NON-NLS-1$
	public static final byte[] notify_progress = ascii ("notify::progress"); // $NON-NLS-1$
	public static final byte[] notify_title = ascii ("notify::title"); // $NON-NLS-1$
	public static final byte[] populate_popup = ascii ("populate-popup"); // $NON-NLS-1$
	public static final byte[] resource_request_starting = ascii ("resource_request_starting"); // $NON-NLS-1$
	public static final byte[] status_bar_text_changed = ascii ("status-bar-text-changed"); // $NON-NLS-1$
	public static final byte[] web_view_ready = ascii ("web-view-ready"); // $NON-NLS-1$
	public static final byte[] window_object_cleared = ascii ("window-object-cleared"); // $NON-NLS-1$

	/** Properties */
	public static final byte[] enable_scripts = ascii ("enable-scripts"); // $NON-NLS-1$
	public static final byte[] enable_universal_access_from_file_uris = ascii ("enable-universal-access-from-file-uris"); // $NON-NLS-1$
	public static final byte[] height = ascii ("height"); // $NON-NLS-1$
	public static final byte[] javascript_can_open_windows_automatically = ascii ("javascript-can-open-windows-automatically"); // $NON-NLS-1$
	public static final byte[] locationbar_visible = ascii ("locationbar-visible"); // $NON-NLS-1$
	public static final byte[] menubar_visible = ascii ("menubar-visible"); // $NON-NLS-1$
	public static final byte[] SOUP_SESSION_PROXY_URI = ascii ("proxy-uri"); // $NON-NLS-1$
	public static final byte[] statusbar_visible = ascii ("statusbar-visible"); // $NON-NLS-1$
	public static final byte[] toolbar_visible = ascii ("toolbar-visible"); // $NON-NLS-1$
	public static final byte[] user_agent = ascii ("user-agent"); // $NON-NLS-1$
	public static final byte[] width = ascii ("width"); // $NON-NLS-1$
	public static final byte[] x = ascii ("x"); // $NON-NLS-1$
	public static final byte[] y = ascii ("y"); // $NON-NLS-1$

protected static byte [] ascii (String name) {
	int length = name.length ();
	char [] chars = new char [length];
	name.getChars (0, length, chars, 0);
	byte [] buffer = new byte [length + 1];
	for (int i=0; i<length; i++) {
		buffer [i] = (byte) chars [i];
	}
	return buffer;
}


/**
 * @param definition cast=(const JSClassDefinition*)
 */
public static final native long /*int*/ _JSClassCreate (long /*int*/ definition);
public static final long /*int*/ JSClassCreate (long /*int*/ definition) {
	lock.lock();
	try {
		return _JSClassCreate (definition);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 */
public static final native long /*int*/ _JSContextGetGlobalObject (long /*int*/ ctx);
public static final long /*int*/ JSContextGetGlobalObject (long /*int*/ ctx) {
	lock.lock();
	try {
		return _JSContextGetGlobalObject (ctx);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param script cast=(JSStringRef)
 * @param thisObject cast=(JSObjectRef)
 * @param sourceURL cast=(JSStringRef)
 * @param exception cast=(JSValueRef *)
 */
public static final native long /*int*/ _JSEvaluateScript (long /*int*/ ctx, long /*int*/ script, long /*int*/ thisObject, long /*int*/ sourceURL, int startingLineNumber, long /*int*/[] exception);
public static final long /*int*/ JSEvaluateScript (long /*int*/ ctx, long /*int*/ script, long /*int*/ thisObject, long /*int*/ sourceURL, int startingLineNumber, long /*int*/[] exception) {
	lock.lock();
	try {
		return _JSEvaluateScript (ctx, script, thisObject, sourceURL, startingLineNumber, exception);
	} finally {
		lock.unlock();
	}
}

/**
 * @param object cast=(JSObjectRef)
 */
public static final native long /*int*/ _JSObjectGetPrivate (long /*int*/ object);
public static final long /*int*/ JSObjectGetPrivate (long /*int*/ object) {
	lock.lock();
	try {
		return _JSObjectGetPrivate (object);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param object cast=(JSObjectRef)
 * @param propertyName cast=(JSStringRef)
 * @param exception cast=(JSValueRef*)
 */
public static final native long /*int*/ _JSObjectGetProperty (long /*int*/ ctx, long /*int*/ object, long /*int*/ propertyName, long /*int*/[] exception);
public static final long /*int*/ JSObjectGetProperty (long /*int*/ ctx, long /*int*/ object, long /*int*/ propertyName, long /*int*/[] exception) {
	lock.lock();
	try {
		return _JSObjectGetProperty (ctx, object, propertyName, exception);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param object cast=(JSObjectRef)
 * @param propertyIndex cast=(unsigned)
 * @param exception cast=(JSValueRef*)
 */
public static final native long /*int*/ _JSObjectGetPropertyAtIndex (long /*int*/ ctx, long /*int*/ object, int propertyIndex, long /*int*/[] exception);
public static final long /*int*/ JSObjectGetPropertyAtIndex (long /*int*/ ctx, long /*int*/ object, int propertyIndex, long /*int*/[] exception) {
	lock.lock();
	try {
		return _JSObjectGetPropertyAtIndex (ctx, object, propertyIndex, exception);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param jsClass cast=(JSClassRef)
 * @param data cast=(void *)
 */
public static final native long /*int*/ _JSObjectMake (long /*int*/ ctx, long /*int*/ jsClass, long /*int*/ data);
public static final long /*int*/ JSObjectMake (long /*int*/ ctx, long /*int*/ jsClass, long /*int*/ data) {
	lock.lock();
	try {
		return _JSObjectMake (ctx, jsClass, data);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param argumentCount cast=(size_t)
 * @param arguments cast=(const struct OpaqueJSValue * const*)
 * @param exception cast=(JSValueRef*)
 */
public static final native long /*int*/ _JSObjectMakeArray (long /*int*/ ctx, long /*int*/ argumentCount, long /*int*/[] arguments, long /*int*/[] exception);
public static final long /*int*/ JSObjectMakeArray (long /*int*/ ctx, long /*int*/ argumentCount, long /*int*/[] arguments, long /*int*/[] exception) {
	lock.lock();
	try {
		return _JSObjectMakeArray (ctx, argumentCount, arguments, exception);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param name cast=(JSStringRef)
 * @param callAsFunction cast=(JSObjectCallAsFunctionCallback)
 */
public static final native long /*int*/ _JSObjectMakeFunctionWithCallback (long /*int*/ ctx, long /*int*/ name, long /*int*/ callAsFunction);
public static final long /*int*/ JSObjectMakeFunctionWithCallback (long /*int*/ ctx, long /*int*/ name, long /*int*/ callAsFunction) {
	lock.lock();
	try {
		return _JSObjectMakeFunctionWithCallback (ctx, name, callAsFunction);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param object cast=(JSObjectRef)
 * @param propertyName cast=(JSStringRef)
 * @param value cast=(JSValueRef)
 * @param attributes cast=(JSPropertyAttributes)
 * @param exception cast=(JSValueRef *)
 */
public static final native void _JSObjectSetProperty (long /*int*/ ctx, long /*int*/ object, long /*int*/ propertyName, long /*int*/ value, int attributes, long /*int*/[] exception);
public static final void JSObjectSetProperty (long /*int*/ ctx, long /*int*/ object, long /*int*/ propertyName, long /*int*/ value, int attributes, long /*int*/[] exception) {
	lock.lock();
	try {
		_JSObjectSetProperty (ctx, object, propertyName, value, attributes, exception);
	} finally {
		lock.unlock();
	}
}

/**
 * @param string cast=(const char *)
 */
public static final native long /*int*/ _JSStringCreateWithUTF8CString (byte[] string);
public static final long /*int*/ JSStringCreateWithUTF8CString (byte[] string) {
	lock.lock();
	try {
		return _JSStringCreateWithUTF8CString (string);
	} finally {
		lock.unlock();
	}
}

/**
 * @param string cast=(JSStringRef)
 */
public static final native long /*int*/ _JSStringGetLength (long /*int*/ string);
public static final long /*int*/ JSStringGetLength (long /*int*/ string) {
	lock.lock();
	try {
		return _JSStringGetLength (string);
	} finally {
		lock.unlock();
	}
}

/**
 * @param string cast=(JSStringRef)
 */
public static final native long /*int*/ _JSStringGetMaximumUTF8CStringSize (long /*int*/ string);
public static final long /*int*/ JSStringGetMaximumUTF8CStringSize (long /*int*/ string) {
	lock.lock();
	try {
		return _JSStringGetMaximumUTF8CStringSize (string);
	} finally {
		lock.unlock();
	}
}

/**
 * @param string cast=(JSStringRef)
 * @param buffer cast=(char *)
 * @param bufferSize cast=(size_t)
 */
public static final native long /*int*/ _JSStringGetUTF8CString (long /*int*/ string, byte[] buffer, long /*int*/ bufferSize);
public static final long /*int*/ JSStringGetUTF8CString (long /*int*/ string, byte[] buffer, long /*int*/ bufferSize) {
	lock.lock();
	try {
		return _JSStringGetUTF8CString (string, buffer, bufferSize);
	} finally {
		lock.unlock();
	}
}

/**
 * @param a cast=(JSStringRef)
 * @param b cast=(const char *)
 */
public static final native int _JSStringIsEqualToUTF8CString (long /*int*/ a, byte[] b);
public static final int JSStringIsEqualToUTF8CString (long /*int*/ a, byte[] b) {
	lock.lock();
	try {
		return _JSStringIsEqualToUTF8CString (a, b);
	} finally {
		lock.unlock();
	}
}

/**
 * @param string cast=(JSStringRef)
 */
public static final native void _JSStringRelease (long /*int*/ string);
public static final void JSStringRelease (long /*int*/ string) {
	lock.lock();
	try {
		_JSStringRelease (string);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param value cast=(JSValueRef)
 */
public static final native int _JSValueGetType (long /*int*/ ctx, long /*int*/ value);
public static final int JSValueGetType (long /*int*/ ctx, long /*int*/ value) {
	lock.lock();
	try {
		return _JSValueGetType (ctx, value);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param value cast=(JSValueRef)
 * @param jsClass cast=(JSClassRef)
 */
public static final native int _JSValueIsObjectOfClass (long /*int*/ ctx, long /*int*/ value, long /*int*/ jsClass);
public static final int JSValueIsObjectOfClass (long /*int*/ ctx, long /*int*/ value, long /*int*/ jsClass) {
	lock.lock();
	try {
		return _JSValueIsObjectOfClass (ctx, value, jsClass);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param bool cast=(bool)
 */
public static final native long /*int*/ _JSValueMakeBoolean (long /*int*/ ctx, int bool);
public static final long /*int*/ JSValueMakeBoolean (long /*int*/ ctx, int bool) {
	lock.lock();
	try {
		return _JSValueMakeBoolean (ctx, bool);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param number cast=(double)
 */
public static final native long /*int*/ _JSValueMakeNumber (long /*int*/ ctx, double number);
public static final long /*int*/ JSValueMakeNumber (long /*int*/ ctx, double number) {
	lock.lock();
	try {
		return _JSValueMakeNumber (ctx, number);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param string cast=(JSStringRef)
 */
public static final native long /*int*/ _JSValueMakeString (long /*int*/ ctx, long /*int*/ string);
public static final long /*int*/ JSValueMakeString (long /*int*/ ctx, long /*int*/ string) {
	lock.lock();
	try {
		return _JSValueMakeString (ctx, string);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 */
public static final native long /*int*/ _JSValueMakeUndefined (long /*int*/ ctx);
public static final long /*int*/ JSValueMakeUndefined (long /*int*/ ctx) {
	lock.lock();
	try {
		return _JSValueMakeUndefined (ctx);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param value cast=(JSValueRef)
 */
public static final native int _JSValueToBoolean (long /*int*/ ctx, long /*int*/ value);
public static final int JSValueToBoolean (long /*int*/ ctx, long /*int*/ value) {
	lock.lock();
	try {
		return _JSValueToBoolean (ctx, value);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param value cast=(JSValueRef)
 * @param exception cast=(JSValueRef*)
 */
public static final native double _JSValueToNumber (long /*int*/ ctx, long /*int*/ value, long /*int*/[] exception);
public static final double JSValueToNumber (long /*int*/ ctx, long /*int*/ value, long /*int*/[] exception) {
	lock.lock();
	try {
		return _JSValueToNumber (ctx, value, exception);
	} finally {
		lock.unlock();
	}
}

/**
 * @param ctx cast=(JSContextRef)
 * @param value cast=(JSValueRef)
 * @param exception cast=(JSValueRef*)
 */
public static final native long /*int*/ _JSValueToStringCopy (long /*int*/ ctx, long /*int*/ value, long /*int*/[] exception);
public static final long /*int*/ JSValueToStringCopy (long /*int*/ ctx, long /*int*/ value, long /*int*/[] exception) {
	lock.lock();
	try {
		return _JSValueToStringCopy (ctx, value, exception);
	} finally {
		lock.unlock();
	}
}

/* --------------------- start libsoup natives --------------------- */

/**
 * @param auth cast=(SoupAuth *)
 * @param username cast=(const char *)
 * @param password cast=(const char *)
 */
public static final native void _soup_auth_authenticate (long /*int*/ auth, byte[] username, byte[] password);
public static final void soup_auth_authenticate (long /*int*/ auth, byte[] username, byte[] password) {
	lock.lock();
	try {
		_soup_auth_authenticate (auth, username, password);
	} finally {
		lock.unlock();
	}
}

/**
 * @param auth cast=(SoupAuth *)
 */
public static final native long /*int*/ _soup_auth_get_host (long /*int*/ auth);
public static final long /*int*/ soup_auth_get_host (long /*int*/ auth) {
	lock.lock();
	try {
		return _soup_auth_get_host (auth);
	} finally {
		lock.unlock();
	}
}

/**
 * @param auth cast=(SoupAuth *)
 */
public static final native long /*int*/ _soup_auth_get_scheme_name (long /*int*/ auth);
public static final long /*int*/ soup_auth_get_scheme_name (long /*int*/ auth) {
	lock.lock();
	try {
		return _soup_auth_get_scheme_name (auth);
	} finally {
		lock.unlock();
	}
}

/**
 * @param jar cast=(SoupCookieJar *)
 * @param cookie cast=(SoupCookie *)
 */
public static final native void _soup_cookie_jar_add_cookie (long /*int*/ jar, long /*int*/ cookie);
public static final void soup_cookie_jar_add_cookie (long /*int*/ jar, long /*int*/ cookie) {
	lock.lock();
	try {
		_soup_cookie_jar_add_cookie (jar, cookie);
	} finally {
		lock.unlock();
	}
}

/**
 * @param jar cast=(SoupCookieJar *)
 */
public static final native long /*int*/ _soup_cookie_jar_all_cookies (long /*int*/ jar);
public static final long /*int*/ soup_cookie_jar_all_cookies (long /*int*/ jar) {
	lock.lock();
	try {
		return _soup_cookie_jar_all_cookies (jar);
	} finally {
		lock.unlock();
	}
}

/**
 * @param jar cast=(SoupCookieJar *)
 * @param cookie cast=(SoupCookie *)
 */
public static final native void _soup_cookie_jar_delete_cookie (long /*int*/ jar, long /*int*/ cookie);
public static final void soup_cookie_jar_delete_cookie (long /*int*/ jar, long /*int*/ cookie) {
	lock.lock();
	try {
		_soup_cookie_jar_delete_cookie (jar, cookie);
	} finally {
		lock.unlock();
	}
}

/**
 * @param jar cast=(SoupCookieJar *)
 * @param uri cast=(SoupURI *)
 */
public static final native long /*int*/ _soup_cookie_jar_get_cookies (long /*int*/ jar, long /*int*/ uri, int for_http);
public static final long /*int*/ soup_cookie_jar_get_cookies (long /*int*/ jar, long /*int*/ uri, int for_http) {
	lock.lock();
	try {
		return _soup_cookie_jar_get_cookies (jar, uri, for_http);
	} finally {
		lock.unlock();
	}
}

public static final native int _soup_cookie_jar_get_type ();
public static final int soup_cookie_jar_get_type () {
	lock.lock();
	try {
		return _soup_cookie_jar_get_type ();
	} finally {
		lock.unlock();
	}
}

/**
 * @param header cast=(const char *)
 * @param origin cast=(SoupURI *)
 */
public static final native long /*int*/ _soup_cookie_parse (byte[] header, long /*int*/ origin);
public static final long /*int*/ soup_cookie_parse (byte[] header, long /*int*/ origin) {
	lock.lock();
	try {
		return _soup_cookie_parse (header, origin);
	} finally {
		lock.unlock();
	}
}

/**
 * @method flags=getter
 * @param cookie cast=(SoupCookie *)
 */
public static final native long /*int*/ _SoupCookie_expires (long /*int*/ cookie);
public static final long /*int*/ SoupCookie_expires (long /*int*/ cookie) {
	lock.lock();
	try {
		return _SoupCookie_expires (cookie);
	} finally {
		lock.unlock();
	}
}

public static final native boolean _SOUP_IS_SESSION (long /*int*/ object);
public static final boolean SOUP_IS_SESSION (long /*int*/ object) {
	lock.lock();
	try {
		return _SOUP_IS_SESSION (object);
	} finally {
		lock.unlock();
	}
}

/**
 * @method flags=setter
 * @param message cast=(SoupMessage *)
 * @param method cast=(const char *)
 */
public static final native void _SoupMessage_method (long /*int*/ message, long /*int*/ method);
public static final void SoupMessage_method (long /*int*/ message, long /*int*/ method) {
	lock.lock();
	try {
		_SoupMessage_method (message, method);
	} finally {
		lock.unlock();
	}
}

/**
 * @method flags=getter
 * @param message cast=(SoupMessage *)
 */
public static final native long /*int*/ _SoupMessage_request_body (long /*int*/ message);
public static final long /*int*/ SoupMessage_request_body (long /*int*/ message) {
	lock.lock();
	try {
		return _SoupMessage_request_body (message);
	} finally {
		lock.unlock();
	}
}

/**
 * @method flags=getter
 * @param message cast=(SoupMessage *)
 */
public static final native long /*int*/ _SoupMessage_request_headers (long /*int*/ message);
public static final long /*int*/ SoupMessage_request_headers (long /*int*/ message) {
	lock.lock();
	try {
		return _SoupMessage_request_headers (message);
	} finally {
		lock.unlock();
	}
}

/**
 * @param body cast=(SoupMessageBody *)
 * @param use cast=(SoupMemoryUse)
 * @param data cast=(gconstpointer)
 * @param length cast=(gsize)
 */
public static final native void _soup_message_body_append (long /*int*/ body, int use, long /*int*/ data, long /*int*/ length);
public static final void soup_message_body_append (long /*int*/ body, int use, long /*int*/ data, long /*int*/ length) {
	lock.lock();
	try {
		_soup_message_body_append (body, use, data, length);
	} finally {
		lock.unlock();
	}
}

/**
 * @param body cast=(SoupMessageBody *)
 */
public static final native void _soup_message_body_flatten (long /*int*/ body);
public static final void soup_message_body_flatten (long /*int*/ body) {
	lock.lock();
	try {
		_soup_message_body_flatten (body);
	} finally {
		lock.unlock();
	}
}

/**
 * @param msg cast=(SoupMessage *)
 */
public static final native long /*int*/ _soup_message_get_uri (long /*int*/ msg);
public static final long /*int*/ soup_message_get_uri (long /*int*/ msg) {
	lock.lock();
	try {
		return _soup_message_get_uri (msg);
	} finally {
		lock.unlock();
	}
}

/**
 * @param headers cast=(SoupMessageHeaders *)
 * @param name cast=(const char *)
 * @param value cast=(const char *)
 */
public static final native void _soup_message_headers_append (long /*int*/ headers, byte[] name, byte[] value);
public static final void soup_message_headers_append (long /*int*/ headers, byte[] name, byte[] value) {
	lock.lock();
	try {
		_soup_message_headers_append (headers, name, value);
	} finally {
		lock.unlock();
	}
}

/**
 * @param session cast=(SoupSession *)
 * @param type cast=(GType)
 */
public static final native void _soup_session_add_feature_by_type (long /*int*/ session, int type);
public static final void soup_session_add_feature_by_type (long /*int*/ session, int type) {
	lock.lock();
	try {
		_soup_session_add_feature_by_type (session, type);
	} finally {
		lock.unlock();
	}
}

/**
 * @param session cast=(SoupSession *)
 * @param feature_type cast=(GType)
 */
public static final native long /*int*/ _soup_session_get_feature (long /*int*/ session, int feature_type);
public static final long /*int*/ soup_session_get_feature (long /*int*/ session, int feature_type) {
	lock.lock();
	try {
		return _soup_session_get_feature (session, feature_type);
	} finally {
		lock.unlock();
	}
}

/**
 * @param feature cast=(SoupSessionFeature *)
 * @param session cast=(SoupSession *)
 */
public static final native void _soup_session_feature_attach (long /*int*/ feature, long /*int*/ session);
public static final void soup_session_feature_attach (long /*int*/ feature, long /*int*/ session) {
	lock.lock();
	try {
		_soup_session_feature_attach (feature, session);
	} finally {
		lock.unlock();
	}
}

/**
 * @param feature cast=(SoupSessionFeature *)
 * @param session cast=(SoupSession *)
 */
public static final native void _soup_session_feature_detach (long /*int*/ feature, long /*int*/ session);
public static final void soup_session_feature_detach (long /*int*/ feature, long /*int*/ session) {
	lock.lock();
	try {
		_soup_session_feature_detach (feature, session);
	} finally {
		lock.unlock();
	}
}

/**
 * @param uri cast=(SoupURI *)
 */
public static final native void _soup_uri_free (long /*int*/ uri);
public static final void soup_uri_free (long /*int*/ uri) {
	lock.lock();
	try {
		_soup_uri_free (uri);
	} finally {
		lock.unlock();
	}
}

/**
 * @param uri_string cast=(const char *)
 */
public static final native long /*int*/ _soup_uri_new (byte[] uri_string);
public static final long /*int*/ soup_uri_new (byte[] uri_string) {
	lock.lock();
	try {
		return _soup_uri_new (uri_string);
	} finally {
		lock.unlock();
	}
}

/**
 * @param uri cast=(SoupURI *)
 */
public static final native long /*int*/ _soup_uri_to_string (long /*int*/ uri, int just_path_and_query);
public static final long /*int*/ soup_uri_to_string (long /*int*/ uri, int just_path_and_query) {
	lock.lock();
	try {
		return _soup_uri_to_string (uri, just_path_and_query);
	} finally {
		lock.unlock();
	}
}

/* --------------------- start WebKitGTK natives --------------------- */

/**
 * @param download cast=(WebKitDownload *)
 */
public static final native void _webkit_download_cancel (long /*int*/ download);
public static final void webkit_download_cancel (long /*int*/ download) {
	lock.lock();
	try {
		_webkit_download_cancel (download);
	} finally {
		lock.unlock();
	}
}

/**
 * @param download cast=(WebKitDownload *)
 */
public static final native long _webkit_download_get_current_size (long /*int*/ download);
public static final long webkit_download_get_current_size (long /*int*/ download) {
	lock.lock();
	try {
		return _webkit_download_get_current_size (download);
	} finally {
		lock.unlock();
	}
}

/**
 * @param download cast=(WebKitDownload *)
 */
public static final native int _webkit_download_get_status (long /*int*/ download);
public static final int webkit_download_get_status (long /*int*/ download) {
	lock.lock();
	try {
		return _webkit_download_get_status (download);
	} finally {
		lock.unlock();
	}
}

/**
 * @param download cast=(WebKitDownload *)
 */
public static final native long /*int*/ _webkit_download_get_suggested_filename (long /*int*/ download);
public static final long /*int*/ webkit_download_get_suggested_filename (long /*int*/ download) {
	lock.lock();
	try {
		return _webkit_download_get_suggested_filename (download);
	} finally {
		lock.unlock();
	}
}

/**
 * @param download cast=(WebKitDownload *)
 */
public static final native long _webkit_download_get_total_size (long /*int*/ download);
public static final long webkit_download_get_total_size (long /*int*/ download) {
	lock.lock();
	try {
		return _webkit_download_get_total_size (download);
	} finally {
		lock.unlock();
	}
}

/**
 * @param download cast=(WebKitDownload *)
 */
public static final native long /*int*/ _webkit_download_get_uri (long /*int*/ download);
public static final long /*int*/ webkit_download_get_uri (long /*int*/ download) {
	lock.lock();
	try {
		return _webkit_download_get_uri (download);
	} finally {
		lock.unlock();
	}
}

/**
 * @param download cast=(WebKitDownload *)
 * @param destination_uri cast=(const gchar *)
 */
public static final native void _webkit_download_set_destination_uri (long /*int*/ download, byte[] destination_uri);
public static final void webkit_download_set_destination_uri (long /*int*/ download, byte[] destination_uri) {
	lock.lock();
	try {
		_webkit_download_set_destination_uri (download, destination_uri);
	} finally {
		lock.unlock();
	}
}

public static final native long /*int*/ _webkit_get_default_session ();
public static final long /*int*/ webkit_get_default_session () {
	lock.lock();
	try {
		return _webkit_get_default_session ();
	} finally {
		lock.unlock();
	}
}

public static final native boolean _WEBKIT_IS_WEB_FRAME (long /*int*/ object);
public static final boolean WEBKIT_IS_WEB_FRAME (long /*int*/ object) {
	lock.lock();
	try {
		return _WEBKIT_IS_WEB_FRAME (object);
	} finally {
		lock.unlock();
	}
}

public static final native int _webkit_major_version ();
public static final int webkit_major_version () {
	lock.lock();
	try {
		return _webkit_major_version ();
	} finally {
		lock.unlock();
	}
}

public static final native int _webkit_micro_version ();
public static final int webkit_micro_version () {
	lock.lock();
	try {
		return _webkit_micro_version ();
	} finally {
		lock.unlock();
	}
}

public static final native int _webkit_minor_version ();
public static final int webkit_minor_version () {
	lock.lock();
	try {
		return _webkit_minor_version ();
	} finally {
		lock.unlock();
	}
}

/**
 * @param request cast=(WebKitNetworkRequest *)
 */
public static final native long /*int*/ _webkit_network_request_get_message (long /*int*/ request);
public static final long /*int*/ webkit_network_request_get_message (long /*int*/ request) {
	lock.lock();
	try {
		return _webkit_network_request_get_message (request);
	} finally {
		lock.unlock();
	}
}

/**
 * @param request cast=(WebKitNetworkRequest *)
 */
public static final native long /*int*/ _webkit_network_request_get_uri (long /*int*/ request);
public static final long /*int*/ webkit_network_request_get_uri (long /*int*/ request) {
	lock.lock();
	try {
		return _webkit_network_request_get_uri (request);
	} finally {
		lock.unlock();
	}
}

/**
 * @param uri cast=(const gchar *)
 */
public static final native long /*int*/ _webkit_network_request_new (byte[] uri);
public static final long /*int*/ webkit_network_request_new (byte[] uri) {
	lock.lock();
	try {
		return _webkit_network_request_new (uri);
	} finally {
		lock.unlock();
	}
}

public static final native int _webkit_soup_auth_dialog_get_type ();
public static final int webkit_soup_auth_dialog_get_type () {
	lock.lock();
	try {
		return _webkit_soup_auth_dialog_get_type ();
	} finally {
		lock.unlock();
	}
}

/**
 * @param data_source cast=(WebKitWebDataSource *)
 */
public static final native long /*int*/ _webkit_web_data_source_get_data (long /*int*/ data_source);
public static final long /*int*/ webkit_web_data_source_get_data (long /*int*/ data_source) {
	lock.lock();
	try {
		return _webkit_web_data_source_get_data (data_source);
	} finally {
		lock.unlock();
	}
}

/**
 * @param data_source cast=(WebKitWebDataSource *)
 */
public static final native long /*int*/ _webkit_web_data_source_get_encoding (long /*int*/ data_source);
public static final long /*int*/ webkit_web_data_source_get_encoding (long /*int*/ data_source) {
	lock.lock();
	try {
		return _webkit_web_data_source_get_encoding (data_source);
	} finally {
		lock.unlock();
	}
}

/**
 * @param frame cast=(WebKitWebFrame *)
 */
public static final native long /*int*/ _webkit_web_frame_get_data_source (long /*int*/ frame);
public static final long /*int*/ webkit_web_frame_get_data_source (long /*int*/ frame) {
	lock.lock();
	try {
		return _webkit_web_frame_get_data_source (frame);
	} finally {
		lock.unlock();
	}
}

/**
 * @param frame cast=(WebKitWebFrame *)
 */
public static final native long /*int*/ _webkit_web_frame_get_global_context (long /*int*/ frame);
public static final long /*int*/ webkit_web_frame_get_global_context (long /*int*/ frame) {
	lock.lock();
	try {
		return _webkit_web_frame_get_global_context (frame);
	} finally {
		lock.unlock();
	}
}

/**
 * @param frame cast=(WebKitWebFrame *)
 */
public static final native int _webkit_web_frame_get_load_status (long /*int*/ frame);
public static final int webkit_web_frame_get_load_status (long /*int*/ frame) {
	lock.lock();
	try {
		return _webkit_web_frame_get_load_status (frame);
	} finally {
		lock.unlock();
	}
}

/**
 * @param frame cast=(WebKitWebFrame *)
 */
public static final native long /*int*/ _webkit_web_frame_get_parent (long /*int*/ frame);
public static final long /*int*/ webkit_web_frame_get_parent (long /*int*/ frame) {
	lock.lock();
	try {
		return _webkit_web_frame_get_parent (frame);
	} finally {
		lock.unlock();
	}
}

/**
 * @param frame cast=(WebKitWebFrame *)
 */
public static final native long /*int*/ _webkit_web_frame_get_title (long /*int*/ frame);
public static final long /*int*/ webkit_web_frame_get_title (long /*int*/ frame) {
	lock.lock();
	try {
		return _webkit_web_frame_get_title (frame);
	} finally {
		lock.unlock();
	}
}

/**
 * @param frame cast=(WebKitWebFrame *)
 */
public static final native long /*int*/ _webkit_web_frame_get_uri (long /*int*/ frame);
public static final long /*int*/ webkit_web_frame_get_uri (long /*int*/ frame) {
	lock.lock();
	try {
		return _webkit_web_frame_get_uri (frame);
	} finally {
		lock.unlock();
	}
}

/**
 * @param frame cast=(WebKitWebFrame *)
 */
public static final native long /*int*/ _webkit_web_frame_get_web_view (long /*int*/ frame);
public static final long /*int*/ webkit_web_frame_get_web_view (long /*int*/ frame) {
	lock.lock();
	try {
		return _webkit_web_frame_get_web_view (frame);
	} finally {
		lock.unlock();
	}
}

/**
 * @param decision cast=(WebKitWebPolicyDecision *)
 */
public static final native void _webkit_web_policy_decision_download (long /*int*/ decision);
public static final void webkit_web_policy_decision_download (long /*int*/ decision) {
	lock.lock();
	try {
		_webkit_web_policy_decision_download (decision);
	} finally {
		lock.unlock();
	}
}

/**
 * @param decision cast=(WebKitWebPolicyDecision *)
 */
public static final native void _webkit_web_policy_decision_ignore (long /*int*/ decision);
public static final void webkit_web_policy_decision_ignore (long /*int*/ decision) {
	lock.lock();
	try {
		_webkit_web_policy_decision_ignore (decision);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native int _webkit_web_view_can_go_back (long /*int*/ web_view);
public static final int webkit_web_view_can_go_back (long /*int*/ web_view) {
	lock.lock();
	try {
		return _webkit_web_view_can_go_back (web_view);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native int _webkit_web_view_can_go_forward (long /*int*/ web_view);
public static final int webkit_web_view_can_go_forward (long /*int*/ web_view) {
	lock.lock();
	try {
		return _webkit_web_view_can_go_forward (web_view);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 * @param mime_type cast=(const gchar *)
 */
public static final native int _webkit_web_view_can_show_mime_type (long /*int*/ web_view, long /*int*/ mime_type);
public static final int webkit_web_view_can_show_mime_type (long /*int*/ web_view, long /*int*/ mime_type) {
	lock.lock();
	try {
		return _webkit_web_view_can_show_mime_type (web_view, mime_type);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 * @param script cast=(const gchar *)
 */
public static final native void _webkit_web_view_execute_script (long /*int*/ web_view, byte[] script);
public static final void webkit_web_view_execute_script (long /*int*/ web_view, byte[] script) {
	lock.lock();
	try {
		_webkit_web_view_execute_script (web_view, script);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native int _webkit_web_view_get_load_status (long /*int*/ web_view);
public static final int webkit_web_view_get_load_status (long /*int*/ web_view) {
	lock.lock();
	try {
		return _webkit_web_view_get_load_status (web_view);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native long /*int*/ _webkit_web_view_get_main_frame (long /*int*/ web_view);
public static final long /*int*/ webkit_web_view_get_main_frame (long /*int*/ web_view) {
	lock.lock();
	try {
		return _webkit_web_view_get_main_frame (web_view);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native double _webkit_web_view_get_progress (long /*int*/ web_view);
public static final double webkit_web_view_get_progress (long /*int*/ web_view) {
	lock.lock();
	try {
		return _webkit_web_view_get_progress (web_view);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native long /*int*/ _webkit_web_view_get_settings (long /*int*/ web_view);
public static final long /*int*/ webkit_web_view_get_settings (long /*int*/ web_view) {
	lock.lock();
	try {
		return _webkit_web_view_get_settings (web_view);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native long /*int*/ _webkit_web_view_get_title (long /*int*/ web_view);
public static final long /*int*/ webkit_web_view_get_title (long /*int*/ web_view) {
	lock.lock();
	try {
		return _webkit_web_view_get_title (web_view);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native long /*int*/ _webkit_web_view_get_uri (long /*int*/ web_view);
public static final long /*int*/ webkit_web_view_get_uri (long /*int*/ web_view) {
	lock.lock();
	try {
		return _webkit_web_view_get_uri (web_view);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native long /*int*/ _webkit_web_view_get_window_features (long /*int*/ web_view);
public static final long /*int*/ webkit_web_view_get_window_features (long /*int*/ web_view) {
	lock.lock();
	try {
		return _webkit_web_view_get_window_features (web_view);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native void _webkit_web_view_go_back (long /*int*/ web_view);
public static final void webkit_web_view_go_back (long /*int*/ web_view) {
	lock.lock();
	try {
		_webkit_web_view_go_back (web_view);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native void _webkit_web_view_go_forward (long /*int*/ web_view);
public static final void webkit_web_view_go_forward (long /*int*/ web_view) {
	lock.lock();
	try {
		_webkit_web_view_go_forward (web_view);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 * @param content cast=(const gchar *)
 * @param mime_type cast=(const gchar *)
 * @param encoding cast=(const gchar *)
 * @param base_uri cast=(const gchar *)
 */
public static final native void _webkit_web_view_load_string (long /*int*/ web_view, byte[] content, byte[] mime_type, byte[] encoding, byte[] base_uri);
public static final void webkit_web_view_load_string (long /*int*/ web_view, byte[] content, byte[] mime_type, byte[] encoding, byte[] base_uri) {
	lock.lock();
	try {
		_webkit_web_view_load_string (web_view, content, mime_type, encoding, base_uri);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 * @param uri cast=(const gchar *)
 */
public static final native void _webkit_web_view_load_uri (long /*int*/ web_view, byte[] uri);
public static final void webkit_web_view_load_uri (long /*int*/ web_view, byte[] uri) {
	lock.lock();
	try {
		_webkit_web_view_load_uri (web_view, uri);
	} finally {
		lock.unlock();
	}
}

public static final native long /*int*/ _webkit_web_view_new ();
public static final long /*int*/ webkit_web_view_new () {
	lock.lock();
	try {
		return _webkit_web_view_new ();
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native void _webkit_web_view_reload (long /*int*/ web_view);
public static final void webkit_web_view_reload (long /*int*/ web_view) {
	lock.lock();
	try {
		_webkit_web_view_reload (web_view);
	} finally {
		lock.unlock();
	}
}

/**
 * @param web_view cast=(WebKitWebView *)
 */
public static final native void _webkit_web_view_stop_loading (long /*int*/ web_view);
public static final void webkit_web_view_stop_loading (long /*int*/ web_view) {
	lock.lock();
	try {
		_webkit_web_view_stop_loading (web_view);
	} finally {
		lock.unlock();
	}
}

/* --------------------- start SWT natives --------------------- */

public static final native int JSClassDefinition_sizeof ();

/**
 * @param dest cast=(void *)
 * @param src cast=(const void *),flags=no_out
 * @param size cast=(size_t)
 */
public static final native void memmove (long /*int*/ dest, JSClassDefinition src, long /*int*/ size);

}
