package toolbox.utils.filetools;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import toolbox.utils.AProcessingTask;

/**
 * Parse received contents as CSV data and store it into ES DB
 * @author laurentml
 *
 */
public abstract class ADbOutstream extends AFileOutstream implements IDbOutstream {
	
	public ADbOutstream(Long fileBytesSize, AProcessingTask parentProcessingTask) {
		super(fileBytesSize,parentProcessingTask);		
	}

	
	}