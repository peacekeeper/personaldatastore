package pds.store.user.impl.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import pds.store.user.Store;
import pds.store.user.StoreException;
import pds.store.user.User;

/**
 * Hibernate-based implementation of the Store interface.
 */
public class DatabaseStore implements Store {

	private static final Log log = LogFactory.getLog(DatabaseStore.class.getName());

	private Properties properties;
	private Configuration configuration;
	private SessionFactory sessionFactory;

	public DatabaseStore(Properties properties) {

		this.properties = properties;
		this.configuration = null;
		this.sessionFactory = null;
	}

	public void init() throws StoreException {

		log.trace("init()");

		try {

			// prepare Hibernate configuration

			this.configuration = new Configuration();

			this.configuration.setProperties(this.properties);

			this.configuration.addClass(pds.store.user.impl.db.DbUser.class);

			// create session factory

			this.initSessionFactory();
		} catch (Exception ex) {

			log.error(ex);
			throw new StoreException("Cannot initialize Hibernate", ex);
		}

		log.trace("Done.");
	}

	private void initSessionFactory() {

		this.sessionFactory = this.configuration.buildSessionFactory();
	}

	public boolean isInitialized() {

		return(this.configuration != null && this.sessionFactory != null);
	}

	public void close() {

		log.trace("close()");

		this.sessionFactory.close();
		this.sessionFactory = null;
	}

	public boolean isClosed() {

		return(this.sessionFactory == null || this.sessionFactory.isClosed());
	}

	/**
	 * Checks if the database connection is still alive;
	 * if not, try to reconnect, then throw exception.
	 * @return The database connection.
	 */
	public SessionFactory getSessionFactory() throws StoreException {

		if (this.sessionFactory != null && ! this.sessionFactory.isClosed()) return(this.sessionFactory);

		this.initSessionFactory();

		if (this.sessionFactory != null && ! this.sessionFactory.isClosed()) return(this.sessionFactory);

		throw new StoreException("Database not available.");
	}

	/**
	 * Allow the connection to be changed externally.
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {

		this.sessionFactory = sessionFactory;
	}

	/*
	 * Common database methods
	 */

	public void updateObject(Object object) throws StoreException {

		log.trace("updateObject()");

		Session session = this.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		// check object

		if (! (object instanceof DbObject)) throw new StoreException("Object is not from this store.");

		// update object

		try {

			session.update(object);
			session.flush();
			session.refresh(object);
			session.getTransaction().commit();
		} catch (Exception ex) {

			if (session.isOpen() && session.getTransaction().isActive()) session.getTransaction().rollback();
			log.error(ex);
			throw new StoreException("Cannot access database.", ex);
		}

		// done

		log.trace("Done.");
	}

	public void deleteObject(Object object) throws StoreException {

		log.trace("deleteObject()");

		Session session = this.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		// check object

		if (! (object instanceof DbObject)) throw new StoreException("Object is not from this store.");

		// delete object

		try {

			reattach(session, object);
			session.delete(object);
			session.flush();
			session.getTransaction().commit();
		} catch (Exception ex) {

			if (session.isOpen() && session.getTransaction().isActive()) session.getTransaction().rollback();
			log.error(ex);
			throw new StoreException("Cannot access database.", ex);
		}
	}

	/*
	 * User methods
	 */

	public User createOrUpdateUser(String identifier, String pass, String recovery, String name, String email, Boolean openid) throws StoreException {

		log.trace("createOrUpdateUser()");

		DbUser user = null;

		Session session = this.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		// create user

		try {

			if (identifier != null) user = DbUser.ByIdentifier(session, identifier);
			if (user == null) user = new DbUser();

			user.setIdentifier(identifier);
			user.setPass(pass);
			user.setRecovery(recovery);
			user.setName(name);
			user.setEmail(email);
			user.setOpenid(openid);

			session.saveOrUpdate(user);
			session.getTransaction().commit();
		} catch (Exception ex) {

			if (session.isOpen() && session.getTransaction().isActive()) session.getTransaction().rollback();
			log.error(ex);
			throw new StoreException("Cannot access database.", ex);
		}

		// done

		log.trace("Done.");
		return(user);
	}

	public User[] listUsers() throws StoreException {

		log.trace("listUsers()");

		Session session = this.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		// get all users

		List<DbUser> users;

		try {

			users = DbUser.All(session);
			session.getTransaction().commit();
		} catch (Exception ex) {

			if (session.isOpen() && session.getTransaction().isActive()) session.getTransaction().rollback();
			log.error(ex);
			throw new StoreException("Cannot access database.", ex);
		}

		// build result list

		User[] result = new ArrayList<DbUser> (users).toArray(new User[users.size()]);

		// done

		log.trace("Done.");
		return(result);
	}

	public User findUser(String identifier) throws StoreException {

		log.trace("findUser()");

		DbUser user;

		Session session = this.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		// get user

		try {

			user = DbUser.ByIdentifier(session, identifier);
			session.getTransaction().commit();
		} catch (Exception ex) {

			if (session.isOpen() && session.getTransaction().isActive()) session.getTransaction().rollback();
			log.error(ex);
			throw new StoreException("Cannot access database.", ex);
		}

		// done

		log.trace("Done.");
		return(user);
	}

	public User[] findUsersByEmail(String email) throws StoreException {

		log.trace("findUsersByEmail()");

		Session session = this.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		// get user

		List<DbUser> users;

		try {

			users = DbUser.ByEmail(session, email);
			session.getTransaction().commit();
		} catch (Exception ex) {

			if (session.isOpen() && session.getTransaction().isActive()) session.getTransaction().rollback();
			log.error(ex);
			throw new StoreException("Cannot access database.", ex);
		}

		// build result list

		User[] result = new ArrayList<DbUser> (users).toArray(new User[users.size()]);

		// done

		log.trace("Done.");
		return(result);
	}

	public User findUserByRecovery(String recovery) throws StoreException {

		log.trace("findUserByRecovery()");

		DbUser user;

		Session session = this.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		// get user

		try {

			user = DbUser.ByRecovery(session, recovery);
			session.getTransaction().commit();
		} catch (Exception ex) {

			if (session.isOpen() && session.getTransaction().isActive()) session.getTransaction().rollback();
			log.error(ex);
			throw new StoreException("Cannot access database.", ex);
		}

		// done

		log.trace("Done.");
		return(user);
	}

	public boolean existsUser(String identifier, String name) throws StoreException {

		log.trace("existsUser()");

		boolean exists = false;

		Session session = this.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		// check user

		try {

			if (identifier != null && DbUser.ByIdentifier(session, identifier) != null) exists = true;
			if (name != null && DbUser.ByName(session, name) != null) exists = true;
			session.getTransaction().commit();
		} catch (Exception ex) {

			if (session.isOpen() && session.getTransaction().isActive()) session.getTransaction().rollback();
			log.error(ex);
			throw new StoreException("Cannot access database.", ex);
		}

		// done

		log.trace("Done.");
		return(exists);
	}

	public User checkUserPassword(String identifier, String pass) throws StoreException {

		log.trace("checkUserPassword()");

		DbUser user;

		Session session = this.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		// check user

		try {

			user = DbUser.ByIdentifierAndPass(session, identifier, pass);
			session.getTransaction().commit();
		} catch (Exception ex) {

			if (session.isOpen() && session.getTransaction().isActive()) session.getTransaction().rollback();
			log.error(ex);
			throw new StoreException("Cannot access database.", ex);
		}

		// done

		log.trace("Done.");
		return(user);
	}

	/*
	 * Utility methods for accessing the database.
	 */

	private static void reattach(Session session, Object object) {

		if (session.contains(object)) return;

		session.lock(object, LockMode.NONE);
	}
}