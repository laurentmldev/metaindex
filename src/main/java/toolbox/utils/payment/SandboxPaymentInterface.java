package toolbox.utils.payment;

/*
This code is Proprietary.
*/


import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SandboxPaymentInterface implements IPaymentInterface {
	
	private Log log = LogFactory.getLog(SandboxPaymentInterface.class);
	
	  
	public PAYMENT_METHOD  getName() { return PAYMENT_METHOD.sandbox; }
	
	public Boolean confirmPayment(String transactionId, Float cost, String paymentDetails) throws IOException {
		
		log.info(transactionId+"\t"+paymentDetails);
		
		return true;
	}
}
