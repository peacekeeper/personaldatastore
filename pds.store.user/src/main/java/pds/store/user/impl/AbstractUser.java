package pds.store.user.impl;

import pds.store.user.User;

public abstract class AbstractUser implements User {
	
	private static final long serialVersionUID = 4322857929032030785L;

	public int compareTo(User other) {

		if (other == this) return(0);
		if (other == null) return(0);

		return(this.getIdentifier().compareTo(other.getIdentifier()));
	
	}
	@Override
	public String toString() {

		return(this.getIdentifier());
	}
	
	@Override
	public boolean equals(Object o) {

		if (o == this) return(true);
		if (o == null) return(false);
		
		if (this.getIdentifier() != null) return(this.getIdentifier().equals(((User)o).getIdentifier()));
		
		return(false);
	}

	@Override
	public int hashCode() {

		if (this.getIdentifier() != null) return(this.getIdentifier().hashCode());
		if (this.getName() != null) return(this.getName().hashCode());
		if (this.getEmail() != null) return(this.getEmail().hashCode());
		if (this.getPass() != null) return(this.getPass().hashCode());
		
		return(0);
	}
}
