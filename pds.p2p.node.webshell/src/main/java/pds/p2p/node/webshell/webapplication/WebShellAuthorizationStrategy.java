package pds.p2p.node.webshell.webapplication;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;

import pds.p2p.api.node.client.DanubeApiClient;

public class WebShellAuthorizationStrategy implements IAuthorizationStrategy {

	@Override
	public <T extends IRequestableComponent> boolean isInstantiationAuthorized(Class<T> componentClass) {

		try {

			if (NeedLoggedInPage.class.isAssignableFrom(componentClass) && (! "1".equals(DanubeApiClient.orionObject.loggedin()))) {

				return false;
			}

			if (NeedConnectedPage.class.isAssignableFrom(componentClass) && (! "1".equals(DanubeApiClient.vegaObject.connected()))) {

				return false;
			}
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		return true;
	}

	@Override
	public boolean isActionAuthorized(Component component, Action action) {

		return true;
	}

	public static interface NeedLoggedInPage {

	}

	public static interface NeedConnectedPage {

	}
}
