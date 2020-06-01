package toolbox.utils.filetools;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.utils.IIdentifiable;

public class FileDescriptor implements IIdentifiable<Integer> {
		private String _name;
		private Long _byteSize;
		private Integer _id;
		
		@Override
		public Integer getId() { return _id; }		
		public void setId(Integer id) {_id=id;}
		
		@Override
		public String getName() { return _name; }
		public void setName(String _name) { this._name = _name; }
		
		public Long getByteSize() { return _byteSize; }
		public void setByteSize(Long _byteSize) { this._byteSize = _byteSize; }	
		
	}