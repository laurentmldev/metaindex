
# 
# 
# Author: Laurent ML - metaindex.fr 2021
# If you find this tools useful somehow, please reference MetaindeX project when possible.
# 
# 
# GNU GENERAL PUBLIC LICENSE
# Version 3, 29 June 2007
# 
# Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
# 
# See full version of LICENSE in <https://fsf.org/>
# 
# 

import sys
import argparse
import os
import re
import math

from difflib import SequenceMatcher as SM
from nltk.util import ngrams
import codecs

import textwrap
from argparse import ArgumentParser, HelpFormatter

### from https://stackoverflow.com/questions/3853722/how-to-insert-newlines-on-argparse-help-text
class RawFormatter(HelpFormatter):
    def _fill_text(self, text, width, indent):
        return "\n".join([textwrap.fill(line, width) for line in textwrap.indent(textwrap.dedent(text), indent).splitlines()])


DESC_TXT='''Merge contents of a CSV file, both horizontally (merge data from several columns) and vertically (merge data from several rows). Merging logic is defined in given config file.'''

###############################

# apply required logic to merge data from a row of input CSV file
def mergeRowData(nameStr,targetData,colsValues,mergeColsDef,mergedSourceLineNb):
	
	if nameStr not in targetData:
		targetData[nameStr]={}

	for targetColName in mergeColsDef:
		for sourceColDef in  mergeColsDef[targetColName]["sourceCols"]:
			colIdx=sourceColDef["colIdx"]
			if colIdx>=len(colsValues):
				print("ERROR: no column ["+str(colIdx)+"] at line "+str(mergedSourceLineNb)+" : "+str(colsValues))
				sys.exit(1)
			sourceColValue=colsValues[colIdx]
			if targetColName not in targetData[nameStr]:
				targetData[nameStr][targetColName]=""
			
			aggrOptions={}
			if "aggrOptions" in mergeColsDef[targetColName]:
				aggrOptions=mergeColsDef[targetColName]["aggrOptions"]

			for algoFunc in mergeColsDef[targetColName]["aggrFuncs"]:
				algoFunc(mergedSourceLineNb,nameStr,targetColName,\
								targetData[nameStr],
								sourceColDef["sourceColName"],\
								sourceColValue,\
								aggrOptions)
			
			

def getMatchScore(searchedStr, withinStr):
	searchedStr_length  = len(searchedStr.split())
	score    = 0
	matchStr = u""

	for ngram in ngrams(withinStr.split(), searchedStr_length + int(.2*searchedStr_length)):
	    withinStr_ngram = u" ".join(ngram)
	    similarity = SM(None, withinStr_ngram, searchedStr).ratio() 
	    if similarity > score:
	        score = similarity
	        matchStr = withinStr_ngram

	return score, matchStr


matchColIdx=None
colsToMergeIndices=[]
colsToAggrIndices=[]

if __name__ == "__main__":
    
	# Define and parse arguments.
	parser = argparse.ArgumentParser(description=DESC_TXT,formatter_class=RawFormatter)
	parser.add_argument("scenario", help="python file containing processing scenario")
	parser.add_argument("inputCsvFile", help="CSV File containing data to merge. First line shall contain columns names")
	parser.add_argument("targetFileName", help="Target file for new merged data")
	parser.add_argument("--treshold_auto", default=1,help="score treshold ([0,1]) required for automatic acceptation of reconciliation result (default is 1)")
	parser.add_argument("--treshold_confirm", default=2, help="score treshold ([0,treshold_auto[) required for confirmation before acceptation of reconciliation result")
	args = parser.parse_args()

	# ensure scenario file exists
	if not os.path.isfile(args.scenario):
		print("ERROR: scenario file not reachable : "+args.scenario)
		sys.exit(1)

	# load scenario
	exec(open(args.scenario).read())

	# get data from scenario
	matchColName=scenario['MatchColName']
	generatedIdPrefix=scenario['IdPrefix']

	# load input CSV file
	dataLines=open(args.inputCsvFile).read().splitlines()
	
	treshold_auto=float(args.treshold_auto)
	treshold_confirm=float(args.treshold_confirm)
	print("Treshold for automatic acceptation : "+str(treshold_auto))
	print("Treshold for manual acceptation : "+str(treshold_confirm))

	# extract position of columns required for merge
	fileColsNames=dataLines[0].split(";")	
	colIdx=0
	mergeColsDef=scenario['MergeColumns']
	nbDetectedMergeCols=0	
	# detect source columns position
	for fileColName in fileColsNames:
		if fileColName==matchColName:
			matchColIdx=colIdx
			print("Matching field : "+fileColName+" at col. "+str(colIdx))

		for targetColName in mergeColsDef:
			for sourceColDef in mergeColsDef[targetColName]["sourceCols"]:
				if fileColName == sourceColDef["sourceColName"]:
					sourceColDef["colIdx"]=colIdx
					nbDetectedMergeCols+=1
					print(targetColName+": field "+fileColName+" found at col. "+str(colIdx))
			
		colIdx=colIdx+1
	
	# ensure that all columns could be found
	if matchColIdx==None:
		print("ERROR: unable to find matchColName column '"+matchColName+"' in first line of CSV file '"+args.inputCsvFile+"' : \n"+dataLines[0])
		sys.exit(1)
	nbUndetectedCols=0
	for targetColName in mergeColsDef:
		for sourceColDef in mergeColsDef[targetColName]["sourceCols"]:
			if "colIdx" not  in sourceColDef:
				print("ERROR: unable to find column '"+sourceColDef["sourceColName"]+"' in first line of CSV file '"+args.inputCsvFile+"' : \n"+dataLines[0])
				nbUndetectedCols+=1
	if nbUndetectedCols>0:
		sys.exit(1)
	
	# resulting data as a map: name -> { <col>:<mergedOrAggrValues }
	resultData={}
	originalLineNb=0
	matchedLineNumbers=[]

	# for each line from input CSV file
	for originalLine in dataLines :

		# ignore first line which is the name of the columns
		if originalLineNb==0:
			originalLineNb+=1
			continue

		# skip line already merged during a previous line processing
		if originalLineNb in matchedLineNumbers:
			originalLineNb+=1
			continue

		originalColums=originalLine.split(";")
		curOriginalString=originalColums[matchColIdx]
		curNormalizedOriginalString=scenario['NormalizeMatchStringFunc'](curOriginalString)

		print("l."+str(originalLineNb+1)+" '"+curOriginalString+"'")
		testedLineNb=0

		# for each line from input CSV file
		for testedLine in dataLines :
			# ignore first line which is the name of the columns
			if testedLineNb==0:
				testedLineNb+=1
				continue

			testedColums=testedLine.split(";")
			testedString=testedColums[matchColIdx]
			normalizedtestString=scenario['NormalizeMatchStringFunc'](testedString)
		
			score,matchstr = getMatchScore(normalizedtestString,curNormalizedOriginalString)
			if score>=treshold_auto:
				print(" 	l."+str(testedLineNb+1)+": "+str(score)+" : \""+matchstr+"\"")
				mergeRowData(curOriginalString,resultData,testedColums,mergeColsDef,testedLineNb)
				matchedLineNumbers+=[testedLineNb]
			elif score>=treshold_confirm :
				print("?	l."+str(testedLineNb+1)+": "+str(score)+" : "+matchstr +"	: \""+curOriginalString+"\"")
				response = input("confirm ? [Y/n]: ")
				if response!='n':
					mergeRowData(curOriginalString,resultData,testedColums,mergeColsDef,testedLineNb)
					matchedLineNumbers+=[testedLineNb]
			
			testedLineNb+=1

		originalLineNb+=1


	# generate resulting CSV file
	fileout= open(args.targetFileName, 'w')
	entryId=0

	hearderLine="id"
	for colName in mergeColsDef:
		hearderLine+=";"+colName
	fileout.write(hearderLine+"\n")

	for strname in resultData:
		cols=resultData[strname]
		entryId+=1
		curLine=generatedIdPrefix+"{:05d}".format(entryId)
		for colName in mergeColsDef:
			curValue=cols[colName]
			if curValue==None:
				curValue=""
			if "finalizeFuncs" in mergeColsDef[colName]:
				for finalizeFunc in mergeColsDef[colName]["finalizeFuncs"]:
					curValue=finalizeFunc(colName,cols)
			curLine+=";"+cols[colName]

		fileout.write(curLine+"\n")

	fileout.close()

	print("created file '"+args.targetFileName+"' with merged data")
		
	sys.exit(0)
