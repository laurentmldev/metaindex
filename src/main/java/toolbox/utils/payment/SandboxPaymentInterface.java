package toolbox.utils.payment;

/*
This code is Proprietary.
*/


import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SandboxPaymentInterface implements IPaymentInterface {
	
	private Log log = LogFactory.getLog(SandboxPaymentInterface.class);
	private Integer _nbTries=0;
	private static final Integer NB_TRIES_BEFORE_SUCCESS=2;
	  
	public PAYMENT_METHOD  getName() { return PAYMENT_METHOD.sandbox; }
	
	
	public Boolean confirmPayment(String transactionId, Float cost, String paymentDetails) throws IOException {
		// simulate a payment service for which transaction would not be available immediatly
		_nbTries++;
		log.info(transactionId+"\t"+paymentDetails);		
		if (_nbTries==NB_TRIES_BEFORE_SUCCESS) { return true; }
		return false;
	}
}
