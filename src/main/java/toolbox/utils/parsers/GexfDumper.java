package toolbox.utils.parsers;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.File;
import static java.nio.file.StandardOpenOption.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AStreamHandler;
import toolbox.utils.IFieldValueMapObject;
import toolbox.utils.filetools.FileSystemUtils;

public class GexfDumper<T extends IFieldValueMapObject> extends AStreamHandler<T>   {

	private Log log = LogFactory.getLog(GexfDumper.class);
	private static final Integer EDGE_TYPE_ATTRIBUTE_ID = 0;
	
	private List<ICatalogTerm> _nodesDataTermsList=new ArrayList<>();
	private List<ICatalogTerm> _edgesTermsList=new ArrayList<>();
	
	Long _nbEdges = 0L;
	
	protected XMLStreamWriter _xmlStreamWriter=null;
	protected XMLStreamWriter _xmlStreamWriterEdges=null;
	
	public GexfDumper(IUserProfileData u, 
						 String name, 
						 Long expectedNbActions,
						 List<ICatalogTerm> dataTermsList,
						 List<ICatalogTerm> edgesTermsList,
						 Date timestamp,
						 String targetFileName) throws DataProcessException { 
		super(u,name,expectedNbActions,timestamp,targetFileName,
						"Items.serverside.gencsvprocess.progress");
		
		this.setNodesDataTermsList(dataTermsList);
		this.setEdgesTermsList(edgesTermsList);		
		
	}
	
	protected String termType2gexfType(RAW_DATATYPE rawtype) {
		if (rawtype==RAW_DATATYPE.Tshort) { return "integer"; }
		if (rawtype==RAW_DATATYPE.Tinteger) { return "long"; }
		if (rawtype==RAW_DATATYPE.Tfloat) { return "double"; }
		
		return "string";	
	}
	
	protected void addCustomAttributes() throws XMLStreamException {};
	@Override
	public void beforeFirst() {
		try {
		
			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
			_xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(
									new FileOutputStream(this.getTargetFileName()), "UTF-8");
			
			
			_xmlStreamWriterEdges = xmlOutputFactory.createXMLStreamWriter(
									new FileOutputStream(this.getTargetFileName()+"_edges"), "UTF-8");
			
            //start writing xml file
            _xmlStreamWriter.writeStartDocument("UTF-8", "1.0");            
            
			// root element            
            _xmlStreamWriter.writeCharacters("\n");
            _xmlStreamWriter.writeStartElement("gexf");
            _xmlStreamWriter.writeAttribute("version","1.2");
            _xmlStreamWriter.writeAttribute("xmlns","http://www.w3.org/1999/xhtml");
            
			// meta element
            _xmlStreamWriter.writeCharacters("\n	");
            _xmlStreamWriter.writeStartElement("meta");
            
						
			// creator
            _xmlStreamWriter.writeCharacters("\n		");
            _xmlStreamWriter.writeStartElement("creator");
            _xmlStreamWriter.writeCharacters("MetaindeX app - metaindex.fr");
            _xmlStreamWriter.writeEndElement();
            
			// description
            _xmlStreamWriter.writeCharacters("\n		");			
            _xmlStreamWriter.writeStartElement("description");
            _xmlStreamWriter.writeCharacters("Contents extracted from MetaindeX app");
            _xmlStreamWriter.writeEndElement();
			
            _xmlStreamWriter.writeCharacters("\n	");
            _xmlStreamWriter.writeEndElement(); // end of meta
            
			// graph
            _xmlStreamWriter.writeCharacters("\n	");
            _xmlStreamWriter.writeStartElement("graph");
            _xmlStreamWriter.writeAttribute("mode","static");
            _xmlStreamWriter.writeAttribute("defaultedgetype","undirected");
			
			// nodes attributes def
            _xmlStreamWriter.writeCharacters("\n		");
            _xmlStreamWriter.writeStartElement("attributes");
            _xmlStreamWriter.writeAttribute("class","node");
            for (ICatalogTerm t : getNodesDataTermsList()) {
				Integer id = t.getId();
				String name = t.getName();
				RAW_DATATYPE type = t.getRawDatatype();
				String gexfType = termType2gexfType(type);
				_xmlStreamWriter.writeCharacters("\n			");
				_xmlStreamWriter.writeStartElement("attribute");
				_xmlStreamWriter.writeAttribute("id",id.toString());
				_xmlStreamWriter.writeAttribute("title",name);
				_xmlStreamWriter.writeAttribute("type",gexfType);				
				_xmlStreamWriter.writeEndElement();
			}
            addCustomAttributes();
            _xmlStreamWriter.writeCharacters("\n		");
            _xmlStreamWriter.writeEndElement();
            
            //edges attributes def
            _xmlStreamWriter.writeCharacters("\n		");
            _xmlStreamWriter.writeStartElement("attributes");
            _xmlStreamWriter.writeAttribute("class","edge");
           {
				Integer id = EDGE_TYPE_ATTRIBUTE_ID;
				String name = "_edge_type_";
				String gexfType = "string";
				_xmlStreamWriter.writeCharacters("\n			");
				_xmlStreamWriter.writeStartElement("attribute");
				_xmlStreamWriter.writeAttribute("id",id.toString());
				_xmlStreamWriter.writeAttribute("title",name);
				_xmlStreamWriter.writeAttribute("type",gexfType);				
				_xmlStreamWriter.writeEndElement();
			}
            _xmlStreamWriter.writeCharacters("\n		");
            _xmlStreamWriter.writeEndElement();
            
            _xmlStreamWriter.writeCharacters("\n		");
            _xmlStreamWriter.writeStartElement("nodes");            
            
            
            // edges root element            
            _xmlStreamWriterEdges.writeCharacters("	");
            _xmlStreamWriterEdges.writeStartElement("edges");
            
            
		} catch (FileNotFoundException | XMLStreamException e1) {
			e1.printStackTrace();
			this.abort();
			return;
		}
	}
	
	@Override
	public void handle(T item) {
		
		try {
			_xmlStreamWriter.writeCharacters("\n			");
			_xmlStreamWriter.writeStartElement("node");
		
			_xmlStreamWriter.writeAttribute("id",item.getId());
			// using ID as label if no custom label defined
			String label = item.getName();
			if (label.length()==0) { label = item.getId(); }
			_xmlStreamWriter.writeAttribute("label",label);
			
			_xmlStreamWriter.writeCharacters("\n				");
			_xmlStreamWriter.writeStartElement("attvalues");
			
			for (ICatalogTerm term : this.getNodesDataTermsList()) {
				
				Object val = item.getValue(term.getName());
				if (val!=null) { 
					_xmlStreamWriter.writeCharacters("\n					");
					_xmlStreamWriter.writeStartElement("attvalue");
					_xmlStreamWriter.writeAttribute("for",term.getId().toString());
					_xmlStreamWriter.writeAttribute("value",val.toString());	
					_xmlStreamWriter.writeEndElement();
				}
				
			}
			_xmlStreamWriter.writeCharacters("\n				");
			_xmlStreamWriter.writeEndElement();//attvalues
			_xmlStreamWriter.writeCharacters("\n			");
			_xmlStreamWriter.writeEndElement();//node
			
			//  write edges
			for (ICatalogTerm term : this.getEdgesTermsList()) {				
				Object val = item.getValue(term.getName());
				if (val!=null) { 
					String idsListStr=val.toString();
					String[] linksIds = idsListStr.split(",");
					for (String curTargetId : linksIds) {
						_xmlStreamWriterEdges.writeCharacters("\n			");
						_xmlStreamWriterEdges.writeStartElement("edge");
						_xmlStreamWriterEdges.writeAttribute("id",_nbEdges.toString());
						_xmlStreamWriterEdges.writeAttribute("source",item.getId());
						_xmlStreamWriterEdges.writeAttribute("target",curTargetId);
						_xmlStreamWriterEdges.writeAttribute("wheight","1");// default edge weight
						
						_xmlStreamWriterEdges.writeCharacters("\n				");
						_xmlStreamWriterEdges.writeStartElement("attvalues");
						
						{
							_xmlStreamWriterEdges.writeCharacters("\n					");
							_xmlStreamWriterEdges.writeStartElement("attvalue");
							_xmlStreamWriterEdges.writeAttribute("for",EDGE_TYPE_ATTRIBUTE_ID.toString());
							_xmlStreamWriterEdges.writeAttribute("value",term.getName());	
							_xmlStreamWriterEdges.writeEndElement();							
						}
						
						_xmlStreamWriterEdges.writeCharacters("\n				");
						_xmlStreamWriterEdges.writeEndElement();//attvalues
						
						_xmlStreamWriterEdges.writeEndElement();// edge
						_nbEdges++;
					}
				}
				
			}			
		
		} catch (XMLStreamException e) {
			e.printStackTrace();
			this.abort();
			return;
		}
	}
	
	@Override
	public void flush() throws IOException {
		try {
			_xmlStreamWriter.flush();
			_xmlStreamWriterEdges.flush();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void afterLast() throws IOException {
		try {
			// finalizing main file (with nodes)
			_xmlStreamWriter.writeCharacters("\n		");
			_xmlStreamWriter.writeEndElement(); // nodes
			_xmlStreamWriter.writeCharacters("\n	");
			
			// don't close it, will append the edges part
			//_xmlStreamWriter.writeEndElement(); // graph
			//_xmlStreamWriter.writeCharacters("\n");
			//_xmlStreamWriter.writeEndElement(); // root
			_xmlStreamWriter.flush();
			_xmlStreamWriter.close();
			
			// finalizing 'edges' file
			_xmlStreamWriterEdges.writeCharacters("\n		");
			_xmlStreamWriterEdges.writeEndElement(); // root
			_xmlStreamWriterEdges.flush();
			_xmlStreamWriterEdges.close();
			
			
			// merging them
			FileSystemUtils.AppendFiles(this.getTargetFileName(),this.getTargetFileName()+"_edges");
		    // terminating file 
	        Files.write(Paths.get(this.getTargetFileName()), "\n	</graph>".getBytes(), APPEND);
	        Files.write(Paths.get(this.getTargetFileName()), "\n</gexf>\n".getBytes(), APPEND);
	        // cleaning tmp edges file
	        File edgesTmpFile= new File(this.getTargetFileName()+"_edges");
	        edgesTmpFile.delete();
	        
		    			
		} catch (XMLStreamException|IOException e) {
			throw new IOException(e);
		}
	}
	

	

	@Override
	public void sendErrorMessageToUser(String msg, List<String> details) {
		getActiveUser().sendGuiErrorMessage(msg, details);		
	}
	@Override
	public void sendErrorMessageToUser(String msg) {
		getActiveUser().sendGuiErrorMessage(msg);		
	}

	public List<ICatalogTerm> getNodesDataTermsList() {
		return _nodesDataTermsList;
	}

	public void setNodesDataTermsList(List<ICatalogTerm> termsList) {
		this._nodesDataTermsList = termsList;
	}
	
	public List<ICatalogTerm> getEdgesTermsList() {
		return _edgesTermsList;
	}

	public void setEdgesTermsList(List<ICatalogTerm> termsList) {
		this._edgesTermsList = termsList;
	}
	
};
