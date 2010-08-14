package pds.store.user;


/**
 * The Store is used for creating, retrieving and managing users.
 */
public interface Store {

	//
	// general store methods
	//

	/**
	 * Initializes the Store.
	 */
	public void init() throws StoreException;

	/**
	 * True, if the Store has been initialized.
	 */
	public boolean isInitialized();

	/**
	 * Closes the Store.
	 */
	public void close();

	/**
	 * True, if the Store has been initialized.
	 */
	public boolean isClosed();

	//
	// common database methods
	//

	/**
	 * Updates an object in the database after modifications have been made.
	 */
	public void updateObject(Object object) throws StoreException;

	/**
	 * Deletes an object from the database.
	 */
	public void deleteObject(Object object) throws StoreException;

	// User methods

	public User createOrUpdateUser(String identifier, String pass, String recovery, String name, String email, Boolean openid) throws StoreException;

	/**
	 * List all users in the Store.
	 */
	public User[] listUsers() throws StoreException;

	/**
	 * Find a user by a given identifier.
	 */
	public User findUser(String identifier) throws StoreException;

	/**
	 * Find all users with a given email address.
	 */
	public User[] findUsersByEmail(String email) throws StoreException;

	/**
	 * Find all users with a given recovery code.
	 */
	public User findUserByRecovery(String recovery) throws StoreException;

	/**
	 * Check if a user with a given identifier or name exists already. Both
	 * parameters may be null.
	 */
	public boolean existsUser(String identifier, String name) throws StoreException;

	/**
	 * Check if the user's hashed password is correct.
	 */
	public User checkUserPassword(String identifier, String pass) throws StoreException;
}
