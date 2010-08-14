package pds.store.user.impl.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import pds.store.user.impl.AbstractUser;


public class DbUser extends AbstractUser implements DbObject {

	private static final long serialVersionUID = 758148303469404859L;

	private Long id;
	private Timestamp timestamp;
	private String identifier;
	private String pass;
	private String recovery;
	private String name;
	private String email;
	private Boolean openid;
	private Map<String, String> attributes;

	DbUser() {

	}

	public Long getId() {
		
		return(this.id);
	}
	
	void setId(Long id) {
		
		this.id = id;
	}
	
	public Timestamp getTimestamp() {
		
		return(this.timestamp);
	}
	
	void setTimestamp(Timestamp timestamp) {
		
		this.timestamp = timestamp;
	}
	
	public String getEmail() {
		
		return(this.email);
	}

	public void setEmail(String email) {
		
		this.email = email;
	}

	public String getIdentifier() {
		
		return(this.identifier);
	}

	public void setIdentifier(String identifier) {
		
		this.identifier = identifier;
	}

	public String getName() {
		
		return(this.name);
	}

	public void setName(String name) {
		
		this.name = name;
	}

	public String getPass() {
		
		return(this.pass);
	}

	public void setPass(String pass) {
		
		this.pass = pass;
	}

	public String getRecovery() {
		
		return(this.recovery);
	}

	public void setRecovery(String recovery) {
		
		this.recovery = recovery;
	}
	
	public Boolean getOpenid() {
		
		return(this.openid);
	}

	public void setOpenid(Boolean openid) {
		
		this.openid = openid;
	}

	public Map<String, String> getAttributes() {
		
		return(this.attributes);
	}

	public void setAttributes(Map<String, String> attributes) {
		
		this.attributes = attributes;
	}

	@SuppressWarnings("unchecked")
	public static List<DbUser> All(Session session) {
		
		List<DbUser> list = session.getNamedQuery(DbUser.class.getName() + ".All")
			.list();
		
		return(list);
	}
	
	public static DbUser ByIdentifier(Session session, String identifier) {
		
		DbUser result = (DbUser) session.getNamedQuery(DbUser.class.getName() + ".ByIdentifier")
			.setString("identifier", identifier)
			.uniqueResult();
	
		return(result);
	}
	
	public static DbUser ByName(Session session, String name) {
		
		DbUser result = (DbUser) session.getNamedQuery(DbUser.class.getName() + ".ByName")
			.setString("name", name)
			.uniqueResult();
	
		return(result);
	}
	
	@SuppressWarnings("unchecked")
	public static List<DbUser> ByEmail(Session session, String email) {
		
		List<DbUser> list = session.getNamedQuery(DbUser.class.getName() + ".ByEmail")
			.setString("email", email)
			.list();
		
		return(list);
	}
	
	public static DbUser ByRecovery(Session session, String recovery) {
		
		DbUser result = (DbUser) session.getNamedQuery(DbUser.class.getName() + ".ByRecovery")
			.setString("recovery", recovery)
			.uniqueResult();
	
		return(result);
	}
	
	public static DbUser ByIdentifierAndPass(Session session, String identifier, String pass) {
		
		DbUser result = (DbUser) session.getNamedQuery(DbUser.class.getName() + ".ByIdentifierAndPass")
			.setString("identifier", identifier)
			.setString("pass", pass)
			.uniqueResult();
	
		return(result);
	}
	
	public static Long Count(Session session) {
		
		Long result = (Long) session.getNamedQuery(DbUser.class.getName() + ".Count")
			.uniqueResult();
		
		return(result);
	}
}
