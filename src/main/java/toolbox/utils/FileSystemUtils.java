package toolbox.utils;

import java.io.File;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemUtils  {
	
	public static Long getTotalSizeBytes(String pathStr) throws IOException {
		Path path = FileSystems.getDefault().getPath(pathStr);
		long size = Files.walk(path).mapToLong( p -> p.toFile().length() ).sum();
		return size;
	}
	
}
