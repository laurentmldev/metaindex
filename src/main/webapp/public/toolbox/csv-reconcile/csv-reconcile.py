
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

from difflib import SequenceMatcher as SM
from nltk.util import ngrams
import codecs

import textwrap
from argparse import ArgumentParser, HelpFormatter

VERSION="1.0"

### from https://stackoverflow.com/questions/3853722/how-to-insert-newlines-on-argparse-help-text
class RawFormatter(HelpFormatter):
    def _fill_text(self, text, width, indent):
        return "\n".join([textwrap.fill(line, width) for line in textwrap.indent(textwrap.dedent(text), indent).splitlines()])


DESC_TXT='''Reconcile data between two given CSV files. 

Detect matching text between first (tested) and second (reference) CSV files.
If matching score is good enough, corresponding ID from reference file is used as reconciled value, and added as a new column in first CSV file.

'''


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

processColsIdx=[]
refMatchColIdx=None
refIdColIdx=None

refMatchStr=[]
refIds=[]

if __name__ == "__main__":
    
	# Define and parse arguments.
	parser = argparse.ArgumentParser(prog="MetaindeX Toolbox - "+__file__+" (from http://metaindex.fr)",description=DESC_TXT,formatter_class=RawFormatter)
	parser.add_argument("csvDataFile", help="CSV File containing data to reconcile")
	parser.add_argument("reconcileColNames", help="column names (from csvDataFile, separated by ',') for which reconciliation shall be performed")
	parser.add_argument("csvRefFile", help="CSV File containing reference data for reconciliation")
	parser.add_argument("idColName", help="Column name containing reference IDs")
	parser.add_argument("matchColName", help="Column name containing text to be matched with contents from reconcileColNames")
	parser.add_argument("--treshold_auto", default=1,help="score treshold ([0,1]) required for automatic acceptation of reconciliation result (default is 1)")
	parser.add_argument("--treshold_confirm", default=2, help="score treshold ([0,treshold_auto[) required for confirmation before acceptation of reconciliation result")
	parser.add_argument("--verbose", default=False,help="show detailed processing steps")
	parser.add_argument("--version", action="version", version="%(prog)s v"+VERSION)
	args = parser.parse_args()

	rowId=0

	dataLines=open(args.csvDataFile).read().splitlines()
	refLines=open(args.csvRefFile).read().splitlines()
	reconcileColNames=args.reconcileColNames.split(',')

	treshold_auto=float(args.treshold_auto)
	treshold_confirm=float(args.treshold_confirm)
	print("Treshold for automatic acceptation : "+str(treshold_auto))
	print("Treshold for manual acceptation : "+str(treshold_confirm))
	# extract index of required columns
	cols=dataLines[0].split(';')
	colIdx=0
	for colName in cols:
		for messyColName in reconcileColNames:
			if colName==messyColName:
				processColsIdx+=[colIdx]
				print("Processed column : "+colName+" at col. "+str(colIdx))
				break;
		colIdx=colIdx+1
	cols=refLines[0].split(';')
	colIdx=0
	for colName in cols:
		if colName==args.idColName:
			refIdColIdx=colIdx
			print("Ref-ID column : "+colName+" at col. "+str(colIdx))
		if colName==args.matchColName:
			refMatchColIdx=colIdx
			print("Ref-Match column : "+colName+" at col. "+str(colIdx))
		colIdx=colIdx+1

	if len(processColsIdx)!=len(reconcileColNames) :
		print("ERROR: "+str(len(reconcileColNames)-len(processColsIdx))+" columns missing from '"+args.reconcileColNames+"' in first line of CSV file '"+args.csvDataFile+"' : \n"+dataLines[0])
		sys.exit(1)
	if refIdColIdx==None:
		print("ERROR: unable to find idColName column '"+args.idColName+"' in first line of CSV file '"+args.csvRefFile+"' : \n"+refLines[0])
		sys.exit(1)
	if refMatchColIdx==None:
		print("ERROR: unable to find matchColName column '"+args.matchColName+"' in first line of CSV file '"+args.csvRefFile+"' : \n"+refLines[0])
		sys.exit(1)
		
	refRowIdx=0
	totalNbRefRows=len(refLines)-1

	dataRowsRefs={}
	dataRowsRefsToBeConfirmed={}

	for refLine in refLines :
		# ignore first line which is the name of the columns
		if refRowIdx==0:
			refRowIdx=refRowIdx+1
			continue
		refCols=refLine.split(';')
		curSearchedNames=refCols[refMatchColIdx].split(",")
		curId=refCols[refIdColIdx]
		for curSearchedName in curSearchedNames:
			print(str(refRowIdx)+"/"+str(totalNbRefRows-1)+" - processing '"+curSearchedName+"' ("+curId+")")

			dataRowIdx=0
			for dataLine in dataLines:
				# add new column containing our result
				if dataRowIdx==0:
					dataRowIdx=dataRowIdx+1
					continue

				if dataRowIdx not in dataRowsRefs:
					dataRowsRefs[dataRowIdx]={}
					dataRowsRefsToBeConfirmed[dataRowIdx]={}
					for processColIdx in processColsIdx:
						dataRowsRefs[dataRowIdx][processColIdx]=set()
						dataRowsRefsToBeConfirmed[dataRowIdx][processColIdx]=set()
					
				dataCols=dataLine.split(';')

				for processColIdx in processColsIdx:
					searchStr=dataCols[processColIdx]

					score,matchstr = getMatchScore(curSearchedName.lower(),searchStr.lower().replace(',' , " , ").replace("d\'"," d' "))
					if score>=treshold_auto:
						print(" 	l."+str(dataRowIdx+1)+": "+str(score)+" : \""+matchstr+"\"")
						dataRowsRefs[dataRowIdx][processColIdx].add(curId)
					elif score>=treshold_confirm :
						print("?	l."+str(dataRowIdx+1)+": "+str(score)+" : "+matchstr +"	: \""+searchStr+"\"")
						response = input("confirm ? [Y/n]: ")
						if response!='n':
							dataRowsRefsToBeConfirmed[dataRowIdx][processColIdx].add(curId)
					elif args.verbose:
						print("x	l."+str(dataRowIdx+1)+": "+str(score)+" : "+matchstr +"	: \""+searchStr+"\"")
						
				dataRowIdx=dataRowIdx+1
				
			refRowIdx=refRowIdx+1

	# dump automatic results
	targetFileName=args.csvDataFile.replace(".csv","-reconciled.csv")
	
	targetfile= open(targetFileName, 'w')
	dataRowIdx=0
	for dataLine in dataLines:	

		if dataRowIdx==0:
			for processColIdx in processColsIdx:
				messyColName=cols[processColIdx]
				dataLine=dataLine+";"+messyColName+"_reconciled"
			targetfile.write(dataLine+"\n")
			dataRowIdx=dataRowIdx+1
			continue

		for processColIdx in processColsIdx:
			dataLine=dataLine+";"
			if dataRowIdx in dataRowsRefs:
				refsList=""
				for ref in dataRowsRefs[dataRowIdx][processColIdx]:
					if len(refsList)>0:
						refsList=refsList+","	
					refsList=refsList+ref					
			
				for ref in dataRowsRefsToBeConfirmed[dataRowIdx][processColIdx]:
					if len(refsList)>0:
						refsList=refsList+","	
					refsList=refsList+ref
					
				dataLine=dataLine+refsList
		
		targetfile.write(dataLine+"\n")
		dataRowIdx=dataRowIdx+1

	targetfile.close()

	print("created file '"+targetFileName+"' with additional reconciled columns")
		

	sys.exit(0)
