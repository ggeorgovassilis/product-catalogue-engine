package org.pce.database.engine.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class Session {

	public JsPreparser jsPreparser = new JsPreparser();
	public Scriptable scope;
	public Context cx;

}
