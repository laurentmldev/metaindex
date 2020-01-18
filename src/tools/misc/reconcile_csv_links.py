#!/usr/local/bin/python3.8

import sys
import argparse
import os
import re

from difflib import SequenceMatcher as SM
from nltk.util import ngrams
import codecs


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

processColIdx=None
refMatchColIdx=None
refIdColIdx=None

refMatchStr=[]
refIds=[]

if __name__ == "__main__":
    
	# Define and parse arguments.
	parser = argparse.ArgumentParser()
	parser.add_argument("csvDataFile", help="CSV File containing data to reconcile")
	parser.add_argument("processColName", help="Column name for which to build reconciliation")
	parser.add_argument("csvRefFile", help="CSV File containing reference data for reconciliation")
	parser.add_argument("idColName", help="Column name containing reconciliation ID")
	parser.add_argument("matchColName", help="Column name for which to build reconciliation")
	parser.add_argument("--treshold_auto", default=0.9,help="score treshold ([0,1]) required for automatic acceptation of reconciliation result")
	parser.add_argument("--treshold_confirm", default=2, help="score treshold ([0,treshold_auto[) required for confirmation before acceptation of reconciliation result")
	args = parser.parse_args()

	rowId=0

	dataLines=open(args.csvDataFile).read().splitlines()
	refLines=open(args.csvRefFile).read().splitlines()

	treshold_auto=float(args.treshold_auto)
	treshold_confirm=float(args.treshold_confirm)
	print("Treshold for automatic acceptation : "+str(treshold_auto))
	print("Treshold for manual acceptation : "+str(treshold_confirm))
	# extract index of required columns
	cols=dataLines[0].split(';')
	colIdx=0
	for colName in cols:
		if colName==args.processColName:
			processColIdx=colIdx
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

	if processColIdx==None:
		print("ERROR: unable to find processColName column '"+args.processColName+"' in first line of CSV file '"+args.csvDataFile+"' : \n"+dataLines[0])
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
		curSearchedName=refCols[refMatchColIdx]
		curId=refCols[refIdColIdx]

		print(str(refRowIdx)+"/"+str(totalNbRefRows-1)+" - processing '"+curSearchedName+"' ("+curId+")")

		dataRowIdx=0
		for dataLine in dataLines:
			# add new column containing our result
			if dataRowIdx==0:
				dataRowIdx=dataRowIdx+1
				continue

			if dataRowIdx not in dataRowsRefs:
				dataRowsRefs[dataRowIdx]=set()
				dataRowsRefsToBeConfirmed[dataRowIdx]=set()

			dataCols=dataLine.split(';')
			searchStr=dataCols[processColIdx]

			score,matchstr = getMatchScore(curSearchedName.lower(),searchStr.lower().replace(',' , " , ").replace("d\'"," d' "))
			if score>=treshold_auto:
				print(" 	l."+str(dataRowIdx+1)+": "+str(score)+" : \""+matchstr+"\"")
				dataRowsRefs[dataRowIdx].add(curId)
			elif score>=treshold_confirm :
				print("?	l."+str(dataRowIdx+1)+": "+str(score)+" : "+matchstr +"	: \""+searchStr+"\"")
				dataRowsRefsToBeConfirmed[dataRowIdx].add(curId)
				#response = input("confirm ? [Y/n]: ")
				#if response!='n':
			dataRowIdx=dataRowIdx+1
			
		refRowIdx=refRowIdx+1

	# dump automatic results
	file1= open('reconciled.csv', 'w')
	dataRowIdx=0
	for dataLine in dataLines:
		dataLine=dataLine+";"
		if dataRowIdx==0:
			dataLine=dataLine+"reconciled"			

		if dataRowIdx in dataRowsRefs:
			nbRefs=0
			for ref in dataRowsRefs[dataRowIdx]:
				if nbRefs>0:
					dataLine=dataLine+","	
				dataLine=dataLine+ref
				nbRefs=nbRefs+1

		if dataRowIdx in dataRowsRefsToBeConfirmed:
			for ref in dataRowsRefsToBeConfirmed[dataRowIdx]:
				if nbRefs>0:
					dataLine=dataLine+","	
				dataLine=dataLine+ref
				nbRefs=nbRefs+1

		file1.write(dataLine+"\n")
		dataRowIdx=dataRowIdx+1

	file1.close()
		

	sys.exit(0)
