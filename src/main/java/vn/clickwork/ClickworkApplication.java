package vn.clickwork;

//import java.security.Key;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Encoders;
//import io.jsonwebtoken.security.Keys;

@SpringBootApplication
public class ClickworkApplication {

	public static void main(String[] args) {
		// Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
		// String base64Key = Encoders.BASE64.encode(key.getEncoded());
		// System.out.print("Generated secret key: " + base64Key);
		SpringApplication.run(ClickworkApplication.class, args);
	}

}
