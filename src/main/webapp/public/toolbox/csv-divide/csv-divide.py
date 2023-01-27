
# 
# 
# Author: Laurent ML - metaindex.fr 2022
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

import textwrap
from argparse import ArgumentParser, HelpFormatter

VERSION="1.0"

### from https://stackoverflow.com/questions/3853722/how-to-insert-newlines-on-argparse-help-text
class RawFormatter(HelpFormatter):
    def _fill_text(self, text, width, indent):
        return "\n".join([textwrap.fill(line, width) for line in textwrap.indent(textwrap.dedent(text), indent).splitlines()])


DESC_TXT='''Split contents of a CSV cells into several lines based on given separator'''

###############################

def extractColumns(colTbl,colsToExport):
	
	exportedLine=""
	nbCols=0
	for fileColName in colsToExport:
		colIdx=colsPosition[fileColName]
		colValue=colTbl[colIdx]
		if nbCols>0:
			exportedLine+=";"
		exportedLine+=colValue
		nbCols+=1

	return exportedLine

if __name__ == "__main__":
    
	# Define and parse arguments.
	parser = argparse.ArgumentParser(prog="MetaindeX Toolbox - "+__file__+" (from http://metaindex.fr)", description=DESC_TXT,formatter_class=RawFormatter)
	parser.add_argument("inputCsvFile", help="CSV File containing data to merge. First line shall contain columns names")
	parser.add_argument("splitCol", help="Name of volumn to divide")
	parser.add_argument("splitSep", help="Separator to detect for split operation")
	parser.add_argument("--idcol", help="If set, add given column with a unique ID for each created line")
	parser.add_argument("targetFileName", help="target file name.")
	parser.add_argument("--colsList", help="List of columns to keep. Default: all")
	parser.add_argument("--version", action="version", version="%(prog)s v"+VERSION)
	args = parser.parse_args()

	# ensure scenario file exists
	if not os.path.isfile(args.inputCsvFile):
		print("ERROR: input file not reachable : "+args.inputCsvFile)
		sys.exit(1)

	# load input CSV file
	dataLines=open(args.inputCsvFile).read().splitlines()
	
	# extract position of columns required for merge
	fileColsNames=dataLines[0].split(";")	
	colsPosition={}
	colsToExport=[]
	colIdx=0
	newEntryNb=0
	
	# detect source columns position
	for fileColName in fileColsNames:
		colsPosition[fileColName]=colIdx
		colsToExport=colsToExport+[fileColName]
		colIdx=colIdx+1
	
	if args.splitCol not in colsPosition:
		print("ERROR: unable to find split column '"+args.splitCol+"' in first line of CSV file '"+args.inputCsvFile+"' : \n"+dataLines[0])
		sys.exit(1)		

	# if custom list to export is given, ensure that all columns could be found
	if args.colsList!=None:
		colsToExport=args.colsList.split(",")
		# ensure that all columns could be found
		for colToExport in colsToExport:
			if colToExport not in colsPosition:
				print("ERROR: unable to find column '"+colToExport+"' in first line of CSV file '"+args.inputCsvFile+"' : \n"+dataLines[0])
				sys.exit(1)	
			else:
				print("column '"+colToExport+"' found at position "+str(colsPosition[colToExport]))
	
	originalLineNb=0
	fileout= open(args.targetFileName, 'w')

	# generate header line (cols names)
	headerLine=""
	if args.idcol!=None:
		headerLine=args.idcol
	for colName in colsToExport:
		if len(headerLine)>0:
			headerLine+=";"		
		headerLine+=colName
	print("headerLine="+headerLine)
	fileout.write("#"+headerLine+"\n")
	

	# for each line from input CSV file
	for originalLine in dataLines :

		originalLine=originalLine.strip()

		# ignore first line (header) 
		if originalLineNb==0 :
			originalLineNb+=1
			continue

		originalColums=originalLine.split(";")
		stringToSplit=originalColums[colsPosition[args.splitCol]].strip()

		# ignore line if column to process is empty
		if len(stringToSplit)==0:
			originalLineNb+=1
			continue

		splitStrings=stringToSplit.split(args.splitSep)
		
		for colVal in splitStrings:
			colVal=colVal.strip()
			if len(colVal)==0:
				continue
			originalColums[colsPosition[args.splitCol]]=colVal
			newLine=extractColumns(originalColums,colsToExport)
			if args.idcol!=None:
				newLine=str(newEntryNb)+";"+newLine
			newEntryNb=newEntryNb+1
			fileout.write(newLine+"\n")

		originalLineNb+=1

	fileout.close()

	print("created file '"+args.targetFileName+"' with merged data")
		
	sys.exit(0)
