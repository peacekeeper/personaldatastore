package pds.core.any;

import javax.servlet.FilterConfig;

import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import pds.core.base.PdsException;
import pds.core.base.PdsInstance;
import pds.core.base.PdsInstanceFactory;

public class AnyPdsInstanceFactory implements PdsInstanceFactory {

	private String[] endpoints;

	@Override
	public void init(FilterConfig filterConfig) throws PdsException {

	}

	public String getTarget(String path) {

		try {

			String target = path;
			while (target.endsWith("/")) target = target.substring(0, target.length() - 1);
			target = new XRI3(target).getAuthority().toString() + "/";

			return target;
		} catch (Exception ex) {

			return null;
		}
	}

	public PdsInstance getPdsInstance(String target) throws PdsException {

		// instantiate a PDS instance for anything

		String xriString = target.substring(0, target.length() - 1);

		XRI3Segment canonical = new XRI3Segment(xriString);

		return new AnyPdsInstance(target, canonical, this.endpoints);
	}

	public String[] getEndpoints() {

		return this.endpoints;
	}

	public void setEndpoints(String[] endpoints) {

		this.endpoints = endpoints;
	}
}
