package toolbox.utils.payment;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SandboxPaymentInterface implements IPaymentInterface {
	
	private Log log = LogFactory.getLog(SandboxPaymentInterface.class);
	private Integer _nbTries=0;
	private static final Integer NB_TRIES_BEFORE_SUCCESS=2;
	  
	public PAYMENT_METHOD  getName() { return PAYMENT_METHOD.sandbox; }
	
	
	public Boolean confirmPayment(String transactionId, Double cost, String paymentDetails) throws IOException {
		// simulate a payment service for which transaction would not be available immediatly
		_nbTries++;
		log.info(transactionId+"\t"+paymentDetails);		
		if (_nbTries==NB_TRIES_BEFORE_SUCCESS) { return true; }
		return false;
	}
}
