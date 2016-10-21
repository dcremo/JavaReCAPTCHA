package it.davidecremonesi.googlerecaptcha;

import java.util.Date;

/** The response is a JSON object:
 * {
 *  "success": true|false,
 * "challenge_ts": timestamp,  // timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
 * "hostname": string,         // the hostname of the site where the reCAPTCHA was solved
 * "error-codes": [...]        // optional
 * 		"missing-input-response",
 *   	"missing-input-secret"
 * }
 */


public class GoogleRecaptchaResponse {
	private boolean success;
	private Date challenge_ts;
	private String hostname;
	private String[] errorCodes;

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Date getChallenge_ts() {
		return challenge_ts;
	}
	public void setChallenge_ts(Date challenge_ts) {
		this.challenge_ts = challenge_ts;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String[] getErrorCodes() {
		return errorCodes;
	}
	public void setErrorCodes(String[] errorCodes) {
		this.errorCodes = errorCodes;
	}
	

}
