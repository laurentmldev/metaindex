

#
# Demo config file for csv-merge.py from MetaindeX Toolbox https://metaindex.fr/webapp/toolbox
# Sample input file can be generated with sample config of csv-gen.py tool.
#
# In this example, we demonstrate how both vertival and horizontal merging can be performed.
#
# Author: Laurent ML - metaindex.fr 2021
# If you find this tools useful somehow, please reference MetaindeX project when possible.
# 
# GNU GENERAL PUBLIC LICENSE
# Version 3, 29 June 2007
# 
# Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
# 
# See full version of LICENSE in <https://fsf.org/>
#

import os

# this map is expected by csv-merge.py tool
scenario={'MatchColName':None, 'IdPrefix':None, 'NormalizeMatchStringFunc':None,  'MergeColumns':None}



# CSV column to be use to identify rows to be merged (vertical merge)
scenario['MatchColName']="curation_location"

# prefix to be used to generate Ids in merged CSV
scenario['IdPrefix']="location_"


### prepare string from match column to be detected in 
def normalizeTextStr(text):
	return text
scenario['NormalizeMatchStringFunc']=normalizeTextStr
	
	
###### Finalize Algos ######

def removeDupsFromList(targetColName,targetRowColumns):
	namesStr=targetRowColumns[targetColName]
	namesList=namesStr.split(",")

	uniqueNamesList=[]
	for name in namesList:
		if name.lower() not in uniqueNamesList:
			uniqueNamesList+=[name.lower().strip()]

	uniqueNamesStr=""
	for name in uniqueNamesList:
		if len(uniqueNamesStr)>0:
			uniqueNamesStr+=","
		curCapName=""
		for word in name.split(" "):
			if len(curCapName)>0:
				curCapName+=" "
			curCapName+=word.capitalize()
		uniqueNamesStr+=curCapName

	targetRowColumns[targetColName]=uniqueNamesStr
		

######### AGGREGATION ALGORITHMS ###########

# options : none
concatLineNb={}
def aggr_firstvalonly(lineNb,targetName,targetColName,targetRowColumns,sourceColName,sourceColValue,options):
			
		curColValue=targetRowColumns[targetColName]
		
		if curColValue==None or len(curColValue)==0:
			targetRowColumns[targetColName]=sourceColValue
		


# options : none
prevFullnameLineNb=None
def aggr_concatfullname(lineNb,targetName,targetColName,targetRowColumns,sourceColName,sourceColValue,options):
		global prevFullnameLineNb
		curColValue=targetRowColumns[targetColName]
		
		if sourceColName=="transcription_author_firstname":
			if len(curColValue)>0:
				curColValue+=","
			prevFullnameLineNb=lineNb
		elif sourceColName=="transcription_author_lastname":
			if prevFullnameLineNb!=lineNb:
				curColValue+=","
			else:
				curColValue+=" "
		
		curColValue+=sourceColValue
		if len(sourceColValue)==0:
			curColValue+="?"

		targetRowColumns[targetColName]=curColValue
			

# options : type:int|float
def aggr_min(lineNb,targetName,targetColName,targetRowColumns,sourceColName,sourceColValue,options):

		curColValue=targetRowColumns[targetColName]
		
		if curColValue==None or len(curColValue)==0:
			targetRowColumns[targetColName]=sourceColValue
		elif sourceColValue==None or len(sourceColValue)==0:
			targetRowColumns[targetColName]=curColValue
		else:
			if options==None or "type" not in options or options["type"]=="int":
				targetRowColumns[targetColName]=str(min(int(curColValue),int(sourceColValue)))			
			else:
				targetRowColumns[targetColName]=str(min(float(curColValue),float(sourceColValue)))			

# options : type:int|float
def aggr_max(lineNb,targetName,targetColName,targetRowColumns,sourceColName,sourceColValue,options):

		curColValue=targetRowColumns[targetColName]
		
		if curColValue==None or len(curColValue)==0:
			targetRowColumns[targetColName]=sourceColValue
		elif sourceColValue==None or len(sourceColValue)==0:
			targetRowColumns[targetColName]=curColValue
		else:
			if options==None or "type" not in options or options["type"]=="int":
				targetRowColumns[targetColName]=str(max(int(curColValue),int(sourceColValue)))			
			else:
				targetRowColumns[targetColName]=str(max(float(curColValue),float(sourceColValue)))			

# options : none
count={}
def aggr_count(lineNb,targetName,targetColName,targetRowColumns,sourceColName,sourceColValue,options):

		curColValue=targetRowColumns[targetColName]
		
		if targetName not in count:
			count[targetName]=0
		
		count[targetName]=count[targetName]+1

		targetRowColumns[targetColName]=str(count[targetName])

####################


####################
scenario['MergeColumns']={
	"curation_location" : { "aggrFuncs":[aggr_firstvalonly], "aggrOptions":{}, "finalizeFuncs":[], "sourceCols":[{ "sourceColName":"curation_location" }] }, 
	"year_min" : { "aggrFuncs":[aggr_min], "aggrOptions":{}, "finalizeFuncs":[], "sourceCols": [ { "sourceColName":"year" } ] },
	"year_max" : { "aggrFuncs":[aggr_max], "aggrOptions":{}, "finalizeFuncs":[], "sourceCols": [ { "sourceColName":"year" } ] },
	"nbArchives": { "aggrFuncs":[aggr_count], "aggrOptions":{}, "finalizeFuncs":[], "sourceCols": [ { "sourceColName":"transcription" } ] },
	"transcription_author" : { "aggrFuncs":[aggr_concatfullname], "aggrOptions":{}, "finalizeFuncs":[removeDupsFromList], "sourceCols": [ { "sourceColName":"transcription_author_firstname" },{ "sourceColName":"transcription_author_lastname" } ] },
}
