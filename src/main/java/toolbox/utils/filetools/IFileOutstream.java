package toolbox.utils.filetools;


import java.util.List;
import java.util.Map;

import toolbox.database.IDbItemsProcessor;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/



import toolbox.exceptions.DataProcessException;
import toolbox.utils.ILockable;
import toolbox.utils.IPair;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;

/**
 * Dump received contents into target (bin) file, preserving order depending on given sequence number
 * @author laurentml
 *
 */
public interface IFileOutstream extends ILockable {
			
	public void write(Integer sequenceNumber, byte[] contents) throws DataProcessException;	
	public void start() throws DataProcessException;
	public void stop() throws DataProcessException;
	public void abort() throws DataProcessException;
	
	public String getName();
	public Long getFileTargetBytesSize();
	public void setFileTargetBytesSize(Long fileTargetBytesSize);
	
	
	}