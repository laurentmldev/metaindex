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

//TODO : remove dep to metaindex classes
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
	
	List<Object> _groupLabels = new ArrayList<>();
	List<Integer> _groupSize = new ArrayList<>();
	
	// cache nodes ids for which group has already been identified
	Map<String,Integer> _targetId2groupIdx=new HashMap<>();
	
	// [linkTerm][srcGroupIdx][targetGroupIdx]=weight	
	private Map<ICatalogTerm, Map<Integer,Map<Integer,Integer> > >_groupEdges=new HashMap<>();
		
	// [linkTerm][targetNodeId][sourceGroupIdx]=weight
	private Map<ICatalogTerm, Map<String,Map<Integer,Integer> > > _pendingEdges = new HashMap<>();
	
	public GexfGroupByDumper(IUserProfileData u, 
						 String name, 
						 Long expectedNbActions,
						 ICatalogTerm groupTerm,
						 List<ICatalogTerm> edgesTermsList,
						 Date timestamp,
						 String targetFileName) throws DataProcessException {
		super(u,name,expectedNbActions,null,null,timestamp,targetFileName);
		
		List<ICatalogTerm> nodesTermsList=new ArrayList<>();
		nodesTermsList.add(groupTerm);
		this.setNodesDataTermsList(nodesTermsList);
		_groupTerm=groupTerm;
		this.setEdgesTermsList(edgesTermsList);		
		
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
		String nodeId=item.getId();		
		Object edgeSrc = item.getValue(_groupTerm.getName());			
		if (edgeSrc==null) { return; }
		Integer curItemGroupIdx=getGroupIdx(edgeSrc);
		_groupSize.set(curItemGroupIdx,_groupSize.get(curItemGroupIdx)+1);		
		_targetId2groupIdx.put(nodeId,curItemGroupIdx);
		
		// identify target node from 'edge terms' in current node
		// if group of this target node is not known yet we keep it in pending edges list
		for (ICatalogTerm edgeTerm : this.getEdgesTermsList()) {
			if (!_groupEdges.containsKey(edgeTerm)) { _groupEdges.put(edgeTerm,new HashMap<>()); }
			if (!_pendingEdges.containsKey(edgeTerm)) { _pendingEdges.put(edgeTerm,new HashMap<>()); }
			
			Object edgeTargetIdObj = item.getValue(edgeTerm.getName());			
			if (edgeTargetIdObj!=null) 
			{ 
				String edgeTargetIds = edgeTargetIdObj.toString();
				for (String edgeTargetIdAndWeight : edgeTargetIds.split(",")) {
					String[] edgeDesc=edgeTargetIdAndWeight.split(":");
					String edgeTargetId=edgeDesc[0];
					Integer edgeTargetWeight=1;
					if (edgeDesc.length>1) { 
						try { edgeTargetWeight=new Integer(edgeDesc[1]); }
						catch (Exception e) {
							this.getActiveUser().sendGuiWarningMessage(
									this.getActiveUser().getText("Items.downloadItems.gexf.badEdgeWeightSyntax",
											item.getId(),edgeTerm.getName(),edgeTargetIdAndWeight,edgeDesc[1])
									);
							edgeTargetWeight=1;
						}
					}
					
					Object targetGroupIdxO=_targetId2groupIdx.get(edgeTargetId);
					// if unknown target
					if (targetGroupIdxO==null) {
						if (_pendingEdges.get(edgeTerm).get(edgeTargetId)==null) { _pendingEdges.get(edgeTerm).put(edgeTargetId, new HashMap<Integer,Integer>()); }
						Integer curPendingWeight = _pendingEdges.get(edgeTerm).get(edgeTargetId).get(curItemGroupIdx);
						if (curPendingWeight==null) { curPendingWeight=0; }
						_pendingEdges.get(edgeTerm).get(edgeTargetId).put(curItemGroupIdx,curPendingWeight+edgeTargetWeight);
						//log.error("### adding link to "+edgeTargetId+":"+edgeTargetWeight+" as pending group "+curItemGroupIdx);
					} else {
						Integer targetGroupIdx=(Integer)targetGroupIdxO;
						if (_groupEdges.get(edgeTerm).get(curItemGroupIdx)==null) { _groupEdges.get(edgeTerm).put(curItemGroupIdx, new HashMap<Integer,Integer>()); }
						if (_groupEdges.get(edgeTerm).get(curItemGroupIdx).get(targetGroupIdx)==null) { _groupEdges.get(edgeTerm).get(curItemGroupIdx).put(targetGroupIdx,0); }
						Integer curWeight=_groupEdges.get(edgeTerm).get(curItemGroupIdx).get(targetGroupIdx);			
						_groupEdges.get(edgeTerm).get(curItemGroupIdx).put(targetGroupIdx,curWeight+edgeTargetWeight);
						//log.error("### adding link to "+edgeTargetId+":"+edgeTargetWeight+" as group "+curItemGroupIdx);
					}
				}
			}
			
			// check for pending edges pointing to this node and if found, 
			// assign them to corresponding target group in edges list
			// adding cumulated weights 
			if (_pendingEdges.get(edgeTerm).get(nodeId)!=null) {
				for (Integer curSrcGroupIdx : _pendingEdges.get(edgeTerm).get(nodeId).keySet()) {
					Integer curPendingWeight =  _pendingEdges.get(edgeTerm).get(nodeId).get(curSrcGroupIdx);
					Integer groupWeight=0;
					if (_groupEdges.get(edgeTerm).get(curSrcGroupIdx)==null) { _groupEdges.get(edgeTerm).put(curSrcGroupIdx, new HashMap<>()); }
					// if current source->dest has already a weight, we cumulate it
					if (_groupEdges.get(edgeTerm).get(curSrcGroupIdx).get(curItemGroupIdx)!=null) {
						groupWeight=new Integer(_groupEdges.get(edgeTerm).get(curSrcGroupIdx).get(curItemGroupIdx));
					} 					
					_groupEdges.get(edgeTerm).get(curSrcGroupIdx).put(curItemGroupIdx,curPendingWeight+groupWeight);
				}
				_pendingEdges.get(edgeTerm).remove(nodeId);
			}
		
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
			for (ICatalogTerm edgeTerm :  _groupEdges.keySet()) {
				Map<Integer,Map<Integer,Integer> > groupEdgesList =  _groupEdges.get(edgeTerm);
				
				for (Integer curSrcGroupIdx : groupEdgesList.keySet()) {
					Map<Integer,Integer> targetGroupWeights=groupEdgesList.get(curSrcGroupIdx);
					for (Integer curTargetGroupIdx : targetGroupWeights.keySet()) {
						Integer weight=targetGroupWeights.get(curTargetGroupIdx);
						_xmlStreamWriterEdges.writeCharacters("\n			");
						_xmlStreamWriterEdges.writeStartElement("edge");
						_xmlStreamWriterEdges.writeAttribute("id", curSrcGroupIdx+"_"+curTargetGroupIdx);
						_xmlStreamWriterEdges.writeAttribute("source",curSrcGroupIdx.toString());
						_xmlStreamWriterEdges.writeAttribute("target",curTargetGroupIdx.toString());
						_xmlStreamWriterEdges.writeAttribute("weight",weight.toString());
						
						_xmlStreamWriterEdges.writeCharacters("\n				");
						_xmlStreamWriterEdges.writeStartElement("attvalues");
						
						{
							_xmlStreamWriterEdges.writeCharacters("\n					");
							_xmlStreamWriterEdges.writeStartElement("attvalue");
							_xmlStreamWriterEdges.writeAttribute("for",GexfDumper.EDGE_TYPE_ATTRIBUTE_ID.toString());
							_xmlStreamWriterEdges.writeAttribute("value",edgeTerm.getName());	
							_xmlStreamWriterEdges.writeEndElement();							
						}
						
						_xmlStreamWriterEdges.writeCharacters("\n				");
						_xmlStreamWriterEdges.writeEndElement();//attvalues
						_xmlStreamWriterEdges.writeCharacters("\n			");
						_xmlStreamWriterEdges.writeEndElement();
						curTargetGroupIdx++;
					}
					curSrcGroupIdx++;
				}
			}
		    			
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
		
		super.afterLast();
	}

};
