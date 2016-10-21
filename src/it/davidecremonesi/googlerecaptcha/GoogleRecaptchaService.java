package it.davidecremonesi.googlerecaptcha;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper per verificare la correttezza di una response captcha
 * https://developers.google.com/recaptcha/docs/verify
 * 
 * @author Davide Cremonesi
 *
 */
public class GoogleRecaptchaService {

	// l'URL per il controllo del captcha, potrebbe passare da giunzione
	private static String k_GOOGLE_URL = "https://www.google.com/recaptcha/api/siteverify";

	protected static final Logger logger = LoggerFactory.getLogger(GoogleRecaptchaService.class);
	private CloseableHttpClient client = null;
	private PoolingHttpClientConnectionManager cm = null;

	/**
	 * Verifica che l'utente abbia risolto correttamente il captcha
	 * 
	 * @param secret
	 *            La chiave secret generata da Google in fase di registrazione
	 * @param capchaResponse
	 *            La response raccolta da frontend
	 * @param maxDeltaTimeSec
	 *            Se maggiore di zero, controlla che non siano passati piu' di
	 *            maxDeltaTimeSec dalla risoluzione del captcha
	 * @param checkHostnamesCsv
	 *            Nomi di host separati da virgola. Se diverso da null o vuoto,
	 *            controlla che la risoluzione del capcha sia avvenuta su uno
	 *            degli host specificati
	 * @return se la request e' valida oppure no
	 * @throws Exception
	 */
	public boolean verificaResponse(String secret, String capchaResponse, long maxDeltaTimeSec,
			String checkHostnamesCsv) throws Exception {
		String out = null;

		/*
		 * Effettuare una chiamata POST a
		 * https://www.google.com/recaptcha/api/siteverify con i parametri
		 * secret Required. The shared key between your site and reCAPTCHA.
		 * response Required. The user response token provided by reCAPTCHA, verifying the user on your site. 
		 * remoteip Optional. The user's IP address
		 */

		logger.info("Preparazione della richiesta POST");
		final RequestBuilder postBuilder = RequestBuilder.post();

		BasicNameValuePair secretPar = new BasicNameValuePair("secret", secret);
		postBuilder.addParameter(secretPar);
		BasicNameValuePair responsePar = new BasicNameValuePair("response", capchaResponse);
		postBuilder.addParameter(responsePar);

		String url = k_GOOGLE_URL;

		logger.info("Preparazione del proxy");

		postBuilder.setUri(url);
		CloseableHttpResponse responseClient = client.execute(postBuilder.build());
		try {
			switch (responseClient.getStatusLine().getStatusCode()) {
			case 200: {
				out = EntityUtils.toString(responseClient.getEntity());
				logger.info("Ricevuta risposta dal servizio.", out);
				break;
			}
			default:
				logger.error("Invalid response code [" + responseClient.getStatusLine().getStatusCode() + "]");
			}
		} finally {
			responseClient.close();
		}

		// map JSON 2 java POJO
		logger.info("Parsing della risposta: " + out);
		GoogleRecaptchaResponse googleResp = new GoogleRecaptchaResponseParser().loadFromJson(out);

		logger.info("Parsing ok");

		boolean retval = googleResp.isSuccess();
		if (retval && maxDeltaTimeSec > 0) {
			long delta = googleResp.getChallenge_ts().getTime() - new Date().getTime();
			retval = retval && (Math.abs(delta) < maxDeltaTimeSec);
		}

		if (checkHostnamesCsv != null && checkHostnamesCsv.length() > 0) {
			retval = checkHostnamesCsv.contains(googleResp.getHostname());
		}
		return retval;
	}

	public GoogleRecaptchaService() {
		initClient();
	}

	@PreDestroy
	public void shutdown() throws Exception {
		cm.close();
		client.close();
	}

	@PostConstruct
	private void initClient() {
		
		/*
		 * This is a very simple initialization of httpclient.
		 * If you wish to personalize behaviour (e.g. proxy) please consult documentation at:
		 * https://hc.apache.org/httpcomponents-client-ga/tutorial/html/index.html
		 */
		cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(100);
		HttpClientBuilder builder = HttpClients.custom();
		builder.setConnectionManager(cm);
		client = builder.build();
	}
}
