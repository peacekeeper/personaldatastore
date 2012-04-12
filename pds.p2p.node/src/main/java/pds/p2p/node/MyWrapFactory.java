package pds.p2p.node;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public class MyWrapFactory extends WrapFactory {

	private static final MyWrapFactory instance;

	static {

		instance = new MyWrapFactory();
	}

	private MyWrapFactory() {

		super();

		this.setJavaPrimitiveWrap(false);
	}

	public static final MyWrapFactory getInstance() {

		return instance;
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

		return wrapped;
	}
}
