package it.davidecremonesi.googlerecaptcha;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GoogleRecaptchaResponseParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testOK() throws JsonParseException, JsonMappingException, IOException, ParseException {
		String jsonOk = "{"
				  + "\"success\": true,"
				  +"\"challenge_ts\": \"2016-10-21T18:03:05Z\","  			// timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
				  + "\"hostname\": \"www.pippo.it\""   // the hostname of the site where the reCAPTCHA was solved
				  + "}";        
		GoogleRecaptchaResponse resp = new GoogleRecaptchaResponseParser().loadFromJson(jsonOk);
		assertTrue(resp.isSuccess());
		assertEquals("www.pippo.it", resp.getHostname());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date d = sdf.parse("2016-10-21T18:03:05Z");
		assertEquals(d, resp.getChallenge_ts());
		assertEquals("www.pippo.it", resp.getHostname());
		assertNull(resp.getErrorCodes());
	}

	@Test
	public void testKO() throws JsonParseException, JsonMappingException, IOException {
		String jsonKo = "{"
				  + "\"success\": false,"
				  +"\"challenge_ts\": \"2016-10-21T18:03:05Z\","  			// timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
				  + "\"hostname\": \"www.pippo.it\","   // the hostname of the site where the reCAPTCHA was solved
				  + "\"error-codes\": ["			// optional
				  +"\"missing-input-secret\","
				  +"\"invalid-input-secret\","
				  +"\"missing-input-response\","
				  +"\"invalid-input-response\""
				  +"]}";        
		GoogleRecaptchaResponse resp = new GoogleRecaptchaResponseParser().loadFromJson(jsonKo);
		assertFalse(resp.isSuccess());
		assertEquals("www.pippo.it", resp.getHostname());
		
		assertEquals(4, resp.getErrorCodes().length);
		assertEquals("missing-input-response", resp.getErrorCodes()[2]);
	}

}
