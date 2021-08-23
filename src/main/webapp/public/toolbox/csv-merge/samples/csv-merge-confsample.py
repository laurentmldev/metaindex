
/*

Author: Laurent ML - metaindex.fr 2021
If you find this tools useful somehow, please reference MetaindeX project when possible.


GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import os

scenario_MatchColName="prenom_nom"
scenario_IdPrefix="person_"

###### Finalize Algos ######

def capitalize(targetColName,targetRowColumns):
	text=targetRowColumns[targetColName]
	result=""
	for word in text.split(" "):
		if len(result)>0:
			result+=" "
		result+=word.capitalize()
	targetRowColumns[targetColName]=result.strip()

def replaceParticuleText(nom):
	nom=nom.lower()
	if "(de)" in nom:
		return "de "+nom.replace(" (de)","")
	if "(d')" in nom:
		return "d'"+nom.replace(" (d')","")
	if "(du)" in nom:
		return "du "+nom.replace(" (du)","")
	if "(de la)" in nom:
		return "de la "+nom.replace(" (de la)","")
	return nom

def replaceParticule(targetColName,targetRowColumns):
	nom=targetRowColumns[targetColName]		
	targetRowColumns[targetColName]=replaceParticuleText(nom)


def finalize_genre(targetColName,targetRowColumns):
	value=targetRowColumns[targetColName]	
	if len(value)==0:
		targetRowColumns[targetColName]="homme"
	targetRowColumns[targetColName]=value

def const_type(targetColName,targetRowColumns):
	targetRowColumns[targetColName]="personne"

def finalize_clean_special_chars(targetColName,targetRowColumns):
	text=targetRowColumns[targetColName]
	targetRowColumns[targetColName]=text.replace("\"","").replace(";",",")

signatures_path_prefix="/Users/laurentml/dev/data/chapeliers/data/signatures/"
def signature_file(targetColName,targetRowColumns):
	nom=targetRowColumns["nom"]
	prenom=targetRowColumns["prenom"]
	annee_debut=targetRowColumns["annee_debut"]

	filename=prenom+"_"+nom+"_"+annee_debut+".png"
	filename=filename.lower().replace("é","e")\
							 .replace("è","e")\
							 .replace("ê","e")\
							 .replace("ô","o")\
							 .replace(" ","_")
	if os.path.isfile(signatures_path_prefix+filename):
		print("#### FILE found ####")
		targetRowColumns[targetColName]=filename
		

######### AGGREGATION ALGORITHMS ###########

# options : firstMatchingRowOnly:bool, separator:string
concatLineNb={}
def aggr_concat(lineNb,targetName,targetColName,targetRowColumns,sourceColName,sourceColValue,options):
			
		curColValue=targetRowColumns[targetColName]
		
		if targetName not in concatLineNb:
			concatLineNb[targetName]={}	
		if targetColName not in concatLineNb[targetName]:
			concatLineNb[targetName][targetColName]=lineNb
		if "firstMatchingRowOnly" in options and options["firstMatchingRowOnly"]==True and lineNb!=concatLineNb[targetName][targetColName]:
			targetRowColumns[targetColName]=curColValue

		if curColValue==None or len(curColValue)==0:
			targetRowColumns[targetColName]=sourceColValue
		elif sourceColValue==None or len(sourceColValue)==0:
			targetRowColumns[targetColName]=curColValue
		else:
			sep=","
			if "separator" in options:
				sep=options["separator"]
			targetRowColumns[targetColName]=curColValue+sep+sourceColValue

# options : none
def aggr_colname(lineNb,targetName,targetColName,targetRowColumns,sourceColName,sourceColValue,options):
		
		curColValue=targetRowColumns[targetColName]
		
		if (sourceColValue==None or len(sourceColValue)>0) and sourceColName not in curColValue:
			if len(curColValue)>0:
				targetRowColumns[targetColName]=curColValue+","+sourceColName
			else:
				targetRowColumns[targetColName]=sourceColName
		else:
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


####################

### prepare string from match column to be detected in 
def scenario_NormalizeMatchString(text):
	return replaceParticuleText(text)

####################

scenario_MergeColumns={
	"type" : { "aggrFuncs":[aggr_concat], "aggrOptions":{"firstMatchingRowOnly":True}, "finalizeFuncs":[const_type], "sourceCols":[{ "sourceColName":"nom" }] }, 
	"nom" : { "aggrFuncs":[aggr_concat], "aggrOptions":{"firstMatchingRowOnly":True}, "finalizeFuncs":[replaceParticule,capitalize], "sourceCols": [ { "sourceColName":"nom" } ] },
	"prenom" : { "aggrFuncs":[aggr_concat], "aggrOptions":{"firstMatchingRowOnly":True}, "finalizeFuncs":[capitalize], "sourceCols": [ { "sourceColName":"prenom" } ] },
	"nom_complet" : { "aggrFuncs":[aggr_concat], "aggrOptions":{"firstMatchingRowOnly":True,"separator":" "}, "finalizeFuncs":[capitalize], "sourceCols": [ { "sourceColName":"prenom" } ,{ "sourceColName":"nom" } ] },
	"archive_id" : { "aggrFuncs":[aggr_concat], "sourceCols": [ { "sourceColName":"id" } ] },	
	"category" : { 	"aggrFuncs":[aggr_colname],
					"sourceCols": [
						{ "sourceColName":"maitre" },
						{ "sourceColName":"marchand" },
						{ "sourceColName":"jure" },
						{ "sourceColName":"compagnon" },
						{ "sourceColName":"apprenti" },
						{ "sourceColName":"bourgeois_de_paris" }
					]
	},
	"genre" : { "aggrFuncs":[aggr_colname], "finalizeFuncs":[finalize_genre], "sourceCols": [ { "sourceColName":"femme" } ] },	
	"annee_debut" : { "aggrFuncs":[aggr_min], "aggrOptions":{"type":"int"}, "sourceCols": [ { "sourceColName":"annee" } ] },
	"annee_fin" : { "aggrFuncs":[aggr_max], "aggrOptions":{"type":"int"}, "sourceCols": [ { "sourceColName":"annee" } ] },	
	"adresse" : { "aggrFuncs":[aggr_concat],  "sourceCols": [ { "sourceColName":"adresse" } ] },
	"rue" : { "aggrFuncs":[aggr_concat],"finalizeFuncs":[capitalize],  "sourceCols": [ { "sourceColName":"rue" } ] },
	"enseigne" : { "aggrFuncs":[aggr_concat], "finalizeFuncs":[capitalize], "sourceCols": [ { "sourceColName":"enseigne" } ] },
	"faubourg" : { "aggrFuncs":[aggr_concat], "finalizeFuncs":[capitalize], "sourceCols": [ { "sourceColName":"faubourg" } ] },
	"paroisse" : { "aggrFuncs":[aggr_concat], "finalizeFuncs":[capitalize], "sourceCols": [ { "sourceColName":"paroisse" } ] },
	"remarques_de_famille" : { "aggrFuncs":[aggr_concat], "finalizeFuncs":[finalize_clean_special_chars], "sourceCols": [ { "sourceColName":"remarques_de_famille" } ] },
	"signatures" : { "aggrFuncs":[aggr_concat],"finalizeFuncs":[signature_file],   "sourceCols": [ { "sourceColName":"signatures" } ] },
}

