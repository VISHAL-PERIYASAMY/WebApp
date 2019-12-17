package webapps.service;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import com.sun.jersey.core.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import java.time.ZoneId;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import webapps.logconstants.WebAppConstant;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.security.Key;
import java.sql.*;
import java.time.LocalDateTime;

@Path("/service")
public class Service {
	private final Logger logger = LoggerFactory.getLogger(Service.class);
	private ResourceBundle myBundle = ResourceBundle.getBundle(WebAppConstant.FILENAME);

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response getFormDataUsingFormParam(@FormParam("name") String name, @FormParam("password") String password)
			throws SQLException {
		Connection connection = null;
		try {
			Class.forName(WebAppConstant.DRIVERCLASS);
			connection = DriverManager.getConnection(WebAppConstant.CONNECTION, WebAppConstant.SQLUSER,
					WebAppConstant.SQLPASSWORD);
			String sql = WebAppConstant.LOGINQUERY;
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, name);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String userPassword = resultSet.getString(WebAppConstant.USERPASSWORD);
				if (password.equals(userPassword)) {
					try {
						return Response.ok().header(AUTHORIZATION, issueToken(name)).build();
					} catch (Exception error) {
						logger.error(
								myBundle.getString(WebAppConstant.ERRORLOG.HM2000E.toString()) + error.getMessage());
					}
				}
			}
		} catch (Exception error) {
			logger.error(myBundle.getString(WebAppConstant.ERRORLOG.HM2001E.toString()) + error.getMessage());
		} finally {
			if (connection != null)
				connection.close();
		}
		return Response.status(401).entity(myBundle.getString(WebAppConstant.ERRORLOG.HM2002E.toString())).build();
	}

	private String issueToken(String login) {
		String keyString = WebAppConstant.KEYSTRING;
		Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, WebAppConstant.KEYVALUE);
		String jwtToken = Jwts.builder().setSubject(login).setIssuedAt(new Date())
				.setExpiration(toDate(LocalDateTime.now().plusMinutes(15L))).signWith(SignatureAlgorithm.HS512, key)
				.compact();
		return jwtToken;

	}

	private Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	@GET
	@Path("/welcome")
	public Response echoWithJWTToken(@Context HttpHeaders httpHeaders) throws SQLException {
		if (httpHeaders.getRequestHeader(WebAppConstant.TOKEN) != null) {
			List<String> token = httpHeaders.getRequestHeader(WebAppConstant.TOKEN);
			if (token.size() >= 1) {
				String tokenValue = token.get(0);
				String[] split_string = tokenValue.split(WebAppConstant.REGEX);
				String base64EncodedBody = split_string[1];
				String body = new String(Base64.decode(base64EncodedBody));
				JSONObject jObject = new JSONObject(body);
				String userName = jObject.getString(WebAppConstant.JSONKEY);
				Connection connection = null;
				try {
					Class.forName(WebAppConstant.DRIVERCLASS);
					connection = DriverManager.getConnection(WebAppConstant.CONNECTION, WebAppConstant.SQLUSER,
							WebAppConstant.SQLPASSWORD);
					String sql = WebAppConstant.TOKENVERIFYQUERY;
					PreparedStatement preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, userName);
					ResultSet resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						try {
							return Response.status(200).entity(userName).build();
						} catch (Exception error) {
							logger.error(myBundle.getString(WebAppConstant.ERRORLOG.HM2000E.toString())
									+ error.getMessage());
						}
					}
				} catch (Exception error) {
					logger.error(myBundle.getString(WebAppConstant.ERRORLOG.HM2001E.toString()) + error.getMessage());
				} finally {
					if (connection != null)
						connection.close();
				}
			}
		}
		return Response.status(401).build();
	}
}
