package webapps.logconstants;

public class WebAppConstant {

	public static final String FILENAME = "WebAppLogMessage";
	public static final String DRIVERCLASS = "com.mysql.jdbc.Driver";
	public static final String CONNECTION = "jdbc:mysql://localhost:3306/webapp";
	public static final String SQLUSER = "root";
	public static final String SQLPASSWORD = "admin";
	public static final String USERPASSWORD = "user_password";
	public static final String LOGINQUERY = "select user_password from login where user_name=?";
	public static final String TOKENVERIFYQUERY = "select * from login where user_name=?";
	public static final String KEYSTRING = "Vishal";
	public static final String KEYVALUE = "DES";
	public static final String TOKEN = "token";
	public static final String REGEX = "\\.";
	public static final String JSONKEY = "sub";

	public static enum ERRORLOG {
		HM2000E, HM2001E, HM2002E
	}

}
