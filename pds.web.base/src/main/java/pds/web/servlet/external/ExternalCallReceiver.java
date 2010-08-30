package pds.web.servlet.external;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pds.web.PDSApplication;

public interface ExternalCallReceiver {

	public void onExternalCall(PDSApplication pdsApplication, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
