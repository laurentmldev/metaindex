package toolbox.utils.filetools;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemUtils  {
	
	public static Long getTotalSizeBytes(String pathStr) throws IOException {
		Integer maxDepth = Integer.MAX_VALUE;
		Path path = FileSystems.getDefault().getPath(pathStr);
		long size = Files.walk(path,maxDepth, FileVisitOption.FOLLOW_LINKS).mapToLong( p -> p.toFile().length() ).sum();
		return size;
	}
	
}
