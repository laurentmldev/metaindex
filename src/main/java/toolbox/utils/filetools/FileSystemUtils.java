package toolbox.utils.filetools;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemUtils  {
	
	public static Long GetTotalSizeBytes(String pathStr) throws IOException {
		Integer maxDepth = Integer.MAX_VALUE;
		Path path = FileSystems.getDefault().getPath(pathStr);
		long size = Files.walk(path,maxDepth, FileVisitOption.FOLLOW_LINKS).mapToLong( p -> p.toFile().length() ).sum();
		return size;
	}
	
	public static void AppendFiles(String fileDest, String fileSource) throws IOException {		
		// solution from https://stackoverflow.com/questions/25546750/merge-huge-files-without-loading-whole-file-into-memory
		Path outFile=Paths.get(fileDest);	    
	    try(FileChannel out=FileChannel.open(outFile, APPEND, WRITE)) {
	        Path inFile=Paths.get(fileSource);
	        try(FileChannel in=FileChannel.open(inFile, READ)) {
	          for(long p=0, l=in.size(); p<l; )
	            p+=in.transferTo(p, l-p, out);
	        }
	    }
	}
	
}
