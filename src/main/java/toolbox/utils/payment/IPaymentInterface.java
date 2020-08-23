package toolbox.utils.payment;

import java.io.IOException;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public interface IPaymentInterface  {
	
	public enum PAYMENT_METHOD { paypal, sandbox }
	PAYMENT_METHOD getName();
	
	Boolean confirmPayment(String transactionId, Float cost, String paymentDetails) throws IOException ;
}
