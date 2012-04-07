package pds.p2p.node.webshell;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyWrapFactory extends WrapFactory {

	private static Logger log = LoggerFactory.getLogger(MyWrapFactory.class);

	public MyWrapFactory() {

		super();

		this.setJavaPrimitiveWrap(false);
	}

	@Override
	public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {

		Object wrapped = super.wrap(cx, scope, obj, staticType);

		if (wrapped instanceof NativeJavaArray) {

			Object[] elements = (Object[]) ((NativeJavaArray) wrapped).unwrap();
			Object[] nativeElements = new Object[elements.length];

			for (int i=0; i<nativeElements.length; i++) {

				nativeElements[i] = this.wrap(cx, scope, elements[i], staticType);
			}

			wrapped = cx.newArray(scope, nativeElements);
		}

		log.debug("Wrapped " + (obj == null ? "null" : obj.getClass()) + " to " + (wrapped == null ? "null" : wrapped.getClass()));

		return wrapped;
	}
}
