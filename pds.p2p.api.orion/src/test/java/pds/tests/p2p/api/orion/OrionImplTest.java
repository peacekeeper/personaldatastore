package pds.tests.p2p.api.orion;

import junit.framework.TestCase;
import pds.p2p.api.Orion;
import pds.p2p.api.orion.OrionFactory;

public class OrionImplTest extends TestCase {

	public void testOrionImpl() throws Exception {

		Orion orion = OrionFactory.getOrion();

		orion.init();
		
		orion.login("=markus", "xxx");

		assertEquals(orion.iname(), "=markus");
		assertEquals(orion.inumber(), "=!b9a9.c0b3.8269.0219");
		assertEquals(orion.xdiUri(), "http://localhost:9090/");
		assertEquals(orion.loggedin(), "1");

		assertEquals(orion.resolve("=markus"), "=!b9a9.c0b3.8269.0219");
		assertEquals(orion.resolve("=markus"), "=!b9a9.c0b3.8269.0219");

		String encrypted = orion.encrypt("hello world", "=!b9a9.c0b3.8269.0219");
		assertEquals(orion.decrypt(encrypted), "hello world");

		String signature = orion.sign("hello world");
		assertEquals(orion.verify("hello world", signature, "=!b9a9.c0b3.8269.0219"), "1");

		assertFalse(orion.guid().equals(orion.guid()));

		String symKey = orion.symGenerateKey();
		String symEncrypted = orion.symEncrypt("hello world", symKey);
		assertEquals(orion.symDecrypt(symEncrypted, symKey), "hello world");
		
		orion.logout();
		assertNull(orion.iname());
		assertNull(orion.inumber());
		assertNull(orion.xdiUri());
		assertNull(orion.loggedin());

		orion.shutdown();
	}
}
