package it.davidecremonesi.googlerecaptcha;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class GoogleRecaptchaResponseParser {
	public static GoogleRecaptchaResponse loadFromJson(String out) throws IOException, JsonParseException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
        GoogleRecaptchaResponse gresp = mapper.readValue(out, GoogleRecaptchaResponse.class);
		return gresp;
	}

}
