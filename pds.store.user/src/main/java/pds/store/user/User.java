package pds.store.user;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * A user.
 * In ibrokerKit, each user has a main identifier as well as a password.
 * - The main identifier normally is the first i-name they register. It can also be
 * an external OpenID, if the user signs in with one.
 * - The password is used for signing in to the i-broker.
 * Each user can have multiple i-names and i-services associated with their account.
 */
public interface User extends Serializable, Comparable<User> {

	/**
	 * Get the internal ID of this user.
	 * @return ID.
	 */
	public Long getId();

	/**
	 * Get the internal timestamp of this user.
	 * @return Timestamp.
	 */
	public Date getTimestamp();

	/**
	 * Get the main identifier of this user. This is normally the first i-name
	 * they register.
	 * @return Identifier.
	 */
	public String getIdentifier();

	/**
	 * Set the main identifier of this user. This is normally the first i-name
	 * they register.
	 * @param identifier Identifier.
	 */
	public void setIdentifier(String identifier);

	/**
	 * Get the name of this user. This is not used by ibrokerKit internally.
	 * @return Name.
	 */
	public String getName();

	/**
	 * Set the name of this user. This is not used by ibrokerKit internally.
	 * @param name Name.
	 */
	public void setName(String name);

	/**
	 * Get the e-mail address of this user. This can be used for looking up a user
	 * if they forget the password.
	 * @return Email.
	 */
	public String getEmail();

	/**
	 * Set the e-mail address of this user. This can be used for looking up a user
	 * if they forget the password.
	 * @param email Email.
	 */
	public void setEmail(String email);

	/**
	 * Get the hashed password of this user.
	 * @return Pass.
	 */
	public String getPass();

	/**
	 * Set the hashed password of this user
	 * @param pass Pass.
	 */
	public void setPass(String pass);

	/**
	 * Get the recovery code of this user. This can be used for resetting the password.
	 * @return Recovery.
	 */
	public String getRecovery();

	/**
	 * Set the recovery code of this user. This can be used for resetting the password.
	 * @param recovery Recovery.
	 */
	public void setRecovery(String recovery);

	/**
	 * Get a flag whether a user is an OpenID user.
	 * @return Openid.
	 */
	public Boolean getOpenid();

	/**
	 * Set a flag whether a user is an OpenID user.
	 * @param openid Openid.
	 */
	public void setOpenid(Boolean openid);

	/**
	 * Get arbitrary key/value pairs. These attributes are not used internally.
	 * @return Attributes.
	 */
	public Map<String, String> getAttributes();

	/**
	 * Set arbitrary key/value pairs. These attributes are not used internally.
	 * @param attributes Attributes.
	 */
	public void setAttributes(Map<String, String> attributes);
}
