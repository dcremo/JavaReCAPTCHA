package it.davidecremonesi.googlerecaptcha;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;

public class GoogleRecaptchaResponseParser {
	public GoogleRecaptchaResponse loadFromJson(String out) throws IOException, JsonParseException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(new MyNameStrategy());
        GoogleRecaptchaResponse gresp = mapper.readValue(out, GoogleRecaptchaResponse.class);
		return gresp;
	}
	
 
	/**
	 * This class avoids the @JsonProperty("error-codes") annotation in the bean
	 * @author Davide Cremonesi
	 *
	 */
	public class MyNameStrategy extends PropertyNamingStrategy
	 {
	  @Override
	  public String nameForSetterMethod(MapperConfig config,
	    AnnotatedMethod method, String defaultName) {
		  if (defaultName.equals("errorCodes")) {
			  return "error-codes";
		  } else {
			  return super.nameForSetterMethod(config, method, defaultName); 
		  }
	  }	  
	 }
}
