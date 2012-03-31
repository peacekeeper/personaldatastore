package pds.core.any;

import javax.servlet.FilterConfig;

import pds.core.base.PdsException;
import pds.core.base.PdsInstance;
import pds.core.base.PdsInstanceFactory;
import xdi2.core.xri3.impl.XRI3;
import xdi2.core.xri3.impl.XRI3Segment;

public class AnyPdsInstanceFactory implements PdsInstanceFactory {

	private String[] endpoints;

	@Override
	public void init(FilterConfig filterConfig) throws PdsException {

	}

	public String getPdsPath(String path) {

		try {

			String pdsPath = path;
			while (pdsPath.endsWith("/")) pdsPath = pdsPath.substring(0, pdsPath.length() - 1);
			pdsPath = new XRI3(pdsPath).getAuthority().toString() + "/";

			return pdsPath;
		} catch (Exception ex) {

			return null;
		}
	}

	public PdsInstance getPdsInstance(String target) throws PdsException {

		// instantiate a PDS instance for anything

		String xriString = target.substring(0, target.length() - 1);

		XRI3Segment canonical = new XRI3Segment(xriString);

		return new AnyPdsInstance(target, canonical, this.endpoints, null, null, null);
	}

	@Override
	public String[] getAllPdsPaths(PdsInstance pdsInstance) throws PdsException {

		return new String[] { ((AnyPdsInstance) pdsInstance).getCanonical().toString() + "/" };
	}

	public String[] getEndpoints() {

		return this.endpoints;
	}

	public void setEndpoints(String[] endpoints) {

		this.endpoints = endpoints;
	}
}
