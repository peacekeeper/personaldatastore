package pds.p2p.api.vega.util;

import java.util.Random;

public class Nonce {

	private static Random random = new Random();

	private String nonce;
	
	public Nonce() { 

		this.calculate();
	}

	public void calculate() {

		this.nonce = Long.toString(System.currentTimeMillis()) + "-" + Long.toString(Math.abs(random.nextLong()));
	}

	@Override
	public String toString() {

		return this.nonce;
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Nonce)) return(false);
		if (object == this) return(true);

		Nonce other = (Nonce) object;

		if (this.nonce == null && other.nonce != null) return(false);
		if (this.nonce != null && ! this.nonce.equals(other.nonce)) return(false);

		return(true);
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.nonce == null ? 0 : this.nonce.hashCode());

		return(hashCode);
	}
}
