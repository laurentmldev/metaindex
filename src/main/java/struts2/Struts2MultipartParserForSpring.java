package metaindex.struts2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.dispatcher.multipart.StrutsUploadedFile;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;



/**
 * Spring framework handle multipart request before Struts2 for security reasons.
 * The pb is that Spring also consume contents of the request instead of simply performing
 * safety checks, and then the request is empty when it reaches struts2 filters.
 * 
 * For that reason we create a specific Struts2 request which will re-populate multipart data
 * for the rest of the processing.
 * @author laurent
 *
 */
public class Struts2MultipartParserForSpring implements MultiPartRequest {
	private Log log = LogFactory.getLog(Struts2MultipartParserForSpring.class);

    private List<String> errors = new ArrayList<String>();

    private MultiValueMap<String, MultipartFile> multipartMap;

    private MultipartHttpServletRequest multipartRequest;

    private MultiValueMap<String, UploadedFile> multiFileMap = new LinkedMultiValueMap<String, UploadedFile>();

    public void parse(HttpServletRequest request, String saveDir)
            throws IOException {
    	
    	multipartRequest =
                WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);

        if(multipartRequest == null) {
            log.warn("Unable to MultipartHttpServletRequest");
            errors.add("Unable to MultipartHttpServletRequest");
            return;
        }
        multipartMap = multipartRequest.getMultiFileMap();
        for(Entry<String, List<MultipartFile>> fileEntry : multipartMap.entrySet()) {
            String fieldName = fileEntry.getKey();
            for(MultipartFile file : fileEntry.getValue()) {
            	File temp = File.createTempFile("upload", ".dat");
                file.transferTo(temp);
                multiFileMap.add(fieldName, new StrutsUploadedFile(temp));
                
            }
        }
       
    }

    public Enumeration<String> getFileParameterNames() {
        return Collections.enumeration(multipartMap.keySet());
    }

    public String[] getContentType(String fieldName) {
        List<MultipartFile> files = multipartMap.get(fieldName);
        if(files == null) {
            return null;
        }
        String[] contentTypes = new String[files.size()];
        int i = 0;
        for(MultipartFile file : files) {
            contentTypes[i++] = file.getContentType();
        }
        return contentTypes;
    }

    public UploadedFile[] getFile(String fieldName) {
    	List<UploadedFile> files = multiFileMap.get(fieldName);
    
        return files == null ? null : files.toArray(new UploadedFile[files.size()]);
    }

    public String[] getFileNames(String fieldName) {
        List<MultipartFile> files = multipartMap.get(fieldName);
        if(files == null) {
            return null;
        }
        String[] fileNames = new String[files.size()];
        int i = 0;
        for(MultipartFile file : files) {
            fileNames[i++] = file.getOriginalFilename();
        }
        return fileNames;
    }

    public String[] getFilesystemName(String fieldName) {
        List<UploadedFile> files = multiFileMap.get(fieldName);
        if(files == null) {
            return null;
        }
        String[] fileNames = new String[files.size()];
        int i = 0;
        for(UploadedFile file : files) {
            fileNames[i++] = file.getName();
        }
        return fileNames;
    }

    public String getParameter(String name) {
        return multipartRequest.getParameter(name);
    }

    public Enumeration<String> getParameterNames() {
        return multipartRequest.getParameterNames();
    }

    public String[] getParameterValues(String name) {
        return multipartRequest.getParameterValues(name);
    }

    public List getErrors() {
        return errors;
    }

    public void cleanUp() {
        for(List<UploadedFile> files : multiFileMap.values()) {
            for(UploadedFile file : files) {
                file.delete();
            }
        }

        // Spring takes care of the original File objects
    }
}
