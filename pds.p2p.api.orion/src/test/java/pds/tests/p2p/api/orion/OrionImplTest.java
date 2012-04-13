package pds.tests.p2p.api.orion;

import java.net.InetAddress;

import junit.framework.TestCase;
import pds.p2p.api.Orion;
import pds.p2p.api.orion.OrionFactory;

public class OrionImplTest extends TestCase {

	private Orion orion;

	@Override
	public void setUp() throws Exception {

		this.orion = OrionFactory.getOrion();

		this.orion.init();
	}

	public void tearDown() throws Exception {

		this.orion.shutdown();
	}

	public void testOrionImpl() throws Exception {

		this.orion.init();
		
		this.orion.login("=markus", "xxx");

		assertEquals(this.orion.iname(), "=markus");
		assertEquals(this.orion.inumber(), "=!b9a9.c0b3.8269.0219");
		assertEquals(this.orion.xdiUri(), "http://" + InetAddress.getLocalHost().getHostName() + ":10100/");
		assertEquals(this.orion.loggedin(), "1");

		assertEquals(this.orion.resolve("=markus"), "=!b9a9.c0b3.8269.0219");
		assertEquals(this.orion.resolve("=markus"), "=!b9a9.c0b3.8269.0219");

		String encrypted = this.orion.encrypt("hello world", "=!b9a9.c0b3.8269.0219");
		assertEquals(this.orion.decrypt(encrypted), "hello world");

		String signature = this.orion.sign("hello world");
		assertEquals(this.orion.verify("hello world", signature, "=!b9a9.c0b3.8269.0219"), "1");

		assertFalse(this.orion.guid().equals(this.orion.guid()));

		String symKey = this.orion.symGenerateKey();
		String symEncrypted = this.orion.symEncrypt("hello world", symKey);
		assertEquals(this.orion.symDecrypt(symEncrypted, symKey), "hello world");
		
		this.orion.logout();
		assertNull(this.orion.iname());
		assertNull(this.orion.inumber());
		assertNull(this.orion.xdiUri());
		assertNull(this.orion.loggedin());

		this.orion.shutdown();
	}
}
