package toolbox.utils.parsers;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.term.ICatalogTerm;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IFieldValueMapObject;


/**
 * 
 * Generate a GEXF graph file, grouping nodes based on contents of given 'group' field,
 * and keeping links weight proportional to amount of individual links detected. 
 * @author laurentml
 *
 */
public class GexfGroupByDumper<T extends IFieldValueMapObject> extends GexfDumper<T>   {

	private Log log = LogFactory.getLog(GexfGroupByDumper.class);
	
	private static final Integer SIZE_ATT_ID = 1;
	ICatalogTerm _groupTerm = null;;
	ICatalogTerm _edgeTerm = null;
	
	List<Object> _groupLabels = new ArrayList<>();
	List<Integer> _groupSize = new ArrayList<>();
	
	// cache noddes ids for which group has already been identified
	Map<String,Integer> _targetId2groupIdx=new HashMap<>();
	
	// [srcGroupIdx][targetGroupIdx]=weight	
	private Map<Integer,Map<Integer,Integer> > _groupEdges=new HashMap<>();
		
	// [targetNodeId][sourceGroupIdx]=weight
	private Map<String,Map<Integer,Integer> >  _pendingEdges = new HashMap<>();
	
	public GexfGroupByDumper(IUserProfileData u, 
						 String name, 
						 Long expectedNbActions,
						 ICatalogTerm groupTerm,
						 ICatalogTerm edgeTerm,
						 Date timestamp,
						 String targetFileName) throws DataProcessException {
		super(u,name,expectedNbActions,null,null,timestamp,targetFileName);
		
		List<ICatalogTerm> nodesTermsList=new ArrayList<>();
		nodesTermsList.add(groupTerm);
		this.setNodesDataTermsList(nodesTermsList);
		_groupTerm=groupTerm;
				
		List<ICatalogTerm> edgesTermsList=new ArrayList<>();
		edgesTermsList.add(edgeTerm);
		this.setEdgesTermsList(edgesTermsList);		
		_edgeTerm=edgeTerm;
	}
	
	private Integer getGroupIdx(Object groupValue) {
		Integer groupIdx = _groupLabels.indexOf(groupValue);
		if (groupIdx==-1) {
			_groupLabels.add(groupValue);
			_groupSize.add(0);
			groupIdx=_groupLabels.size()-1;
		}
		
		return groupIdx;
	}
	
	
	@Override
	public void handle(T item) {
		
		// identify src group from 'group term' in current node
		Object edgeSrc = item.getValue(_groupTerm.getName());			
		if (edgeSrc==null) { return; }
		Integer curItemGroupIdx=getGroupIdx(edgeSrc);
		_groupSize.set(curItemGroupIdx,_groupSize.get(curItemGroupIdx)+1);
		
		// identify target node from 'edge term' in current node
		// if group of this target node is not known yet we keep it in pending edges list
		Object edgeTargetIdO = item.getValue(_edgeTerm.getName());			
		if (edgeTargetIdO!=null) 
		{ 
			String edgeTargetIds = edgeTargetIdO.toString();
			for (String edgeTargetId : edgeTargetIds.split(",")) {
				
				Object targetGroupIdxO=_targetId2groupIdx.get(edgeTargetId);
				// if unknown target
				if (targetGroupIdxO==null) {
					if (_pendingEdges.get(edgeTargetId)==null) { _pendingEdges.put(edgeTargetId, new HashMap<Integer,Integer>()); }
					Integer curPendingWeight = _pendingEdges.get(edgeTargetId).get(curItemGroupIdx);
					if (curPendingWeight==null) { curPendingWeight=0; }
					_pendingEdges.get(edgeTargetId).put(curItemGroupIdx,curPendingWeight+1);
				} else {
					Integer targetGroupIdx=(Integer)targetGroupIdxO;
					if (_groupEdges.get(curItemGroupIdx)==null) { _groupEdges.put(curItemGroupIdx, new HashMap<Integer,Integer>()); }
					if (_groupEdges.get(curItemGroupIdx).get(targetGroupIdx)==null) { _groupEdges.get(curItemGroupIdx).put(targetGroupIdx,0); }
					Integer curWeight=_groupEdges.get(curItemGroupIdx).get(targetGroupIdx);			
					_groupEdges.get(curItemGroupIdx).put(targetGroupIdx,curWeight+1);
				}
			}
		}
		// check for pending edges pointing to this node and if found, 
		// assign them to corresponding target group in edges list
		String nodeId=item.getId();
		if (_pendingEdges.get(nodeId)!=null) {
			for (Integer curSrcGroupIdx : _pendingEdges.get(nodeId).keySet()) {
				Integer weight =  _pendingEdges.get(nodeId).get(curSrcGroupIdx);
				if (_groupEdges.get(curSrcGroupIdx)==null) { _groupEdges.put(curSrcGroupIdx, new HashMap<>()); } 
				_groupEdges.get(curSrcGroupIdx).put(curItemGroupIdx,weight);	
				_targetId2groupIdx.put(nodeId,curItemGroupIdx);				
			}
			_pendingEdges.remove(nodeId);
		}
		
	}
	
	@Override
	public void addCustomAttributes() throws XMLStreamException {
		// group size
		Integer id = SIZE_ATT_ID;
		String name = "size";
		String gexfType = "integer";
		_xmlStreamWriter.writeCharacters("\n			");
		_xmlStreamWriter.writeStartElement("attribute");
		_xmlStreamWriter.writeAttribute("id",id.toString());
		_xmlStreamWriter.writeAttribute("title",name);
		_xmlStreamWriter.writeAttribute("type",gexfType);				
		_xmlStreamWriter.writeEndElement();
		
			
	}
	@Override
	public void afterLast() throws IOException {
		
		try {
			Integer groupIdx=0;
			// write nodes of all detected groups
			for (Object groupLabelO : _groupLabels) {				
				_xmlStreamWriter.writeCharacters("\n			");
				_xmlStreamWriter.writeStartElement("node");			
				_xmlStreamWriter.writeAttribute("id",groupIdx.toString());
				// using ID as label if no custom label defined
				String label = groupLabelO.toString();
				if (label.length()==0) { label = groupIdx.toString(); }
				_xmlStreamWriter.writeAttribute("label",label);			
				
				_xmlStreamWriter.writeCharacters("\n				");
				_xmlStreamWriter.writeStartElement("attvalues");
				
				// size
				_xmlStreamWriter.writeCharacters("\n					");
				_xmlStreamWriter.writeStartElement("attvalue");
				_xmlStreamWriter.writeAttribute("for",SIZE_ATT_ID.toString());
				_xmlStreamWriter.writeAttribute("value",_groupSize.get(groupIdx).toString());
				_xmlStreamWriter.writeCharacters("\n					");
				_xmlStreamWriter.writeEndElement();// attvalue
				
				_xmlStreamWriter.writeCharacters("\n				");
				_xmlStreamWriter.writeEndElement();//attvalues	
				
				_xmlStreamWriter.writeCharacters("\n			");
				_xmlStreamWriter.writeEndElement();//node				
				groupIdx++;
			}
			
			// write all detected edges between thoses group nodes
			for (Integer curSrcGroupIdx : _groupEdges.keySet()) {
				Map<Integer,Integer> targetGroupWeights=_groupEdges.get(curSrcGroupIdx);
				for (Integer curTargetGroupIdx : targetGroupWeights.keySet()) {
					Integer weight=targetGroupWeights.get(curTargetGroupIdx);
					_xmlStreamWriterEdges.writeCharacters("\n			");
					_xmlStreamWriterEdges.writeStartElement("edge");
					_xmlStreamWriterEdges.writeAttribute("id", curSrcGroupIdx+"_"+curTargetGroupIdx);
					_xmlStreamWriterEdges.writeAttribute("source",curSrcGroupIdx.toString());
					_xmlStreamWriterEdges.writeAttribute("target",curTargetGroupIdx.toString());
					_xmlStreamWriterEdges.writeAttribute("weight",weight.toString());// default edge weight
					_xmlStreamWriterEdges.writeEndElement();
					curTargetGroupIdx++;
				}
				curSrcGroupIdx++;
			}
		    			
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
		
		super.afterLast();
	}

};
