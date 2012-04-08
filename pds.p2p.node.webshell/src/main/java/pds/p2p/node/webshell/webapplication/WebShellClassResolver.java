package pds.p2p.node.webshell.webapplication;

import org.apache.wicket.application.AbstractClassResolver;
import org.apache.wicket.application.IClassResolver;

public class WebShellClassResolver extends AbstractClassResolver implements IClassResolver {

	@Override
	protected ClassLoader getClassLoader() {

		return this.getClass().getClassLoader();
	}
}
