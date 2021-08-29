
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
import os.path

import textwrap
from argparse import ArgumentParser, HelpFormatter

VERSION="1.1"

### from https://stackoverflow.com/questions/3853722/how-to-insert-newlines-on-argparse-help-text
class RawFormatter(HelpFormatter):
    def _fill_text(self, text, width, indent):
        return "\n".join([textwrap.fill(line, width) for line in textwrap.indent(textwrap.dedent(text), indent).splitlines()])


DESC_TXT='''Create a test CSV file with statistical control of generated contents. 
This is tool is useful to create test files of arbitrary size with precise control
of occurrences of each possible value.

Given config file shall define a 'fields' map defining, the keys begin the CSV columns, and values being functions
returning expected values.

'''

if __name__ == "__main__":
    
    # Define and parse arguments.
    parser = argparse.ArgumentParser(prog="MetaindeX Toolbox - "+__file__+" (from http://metaindex.fr)",description=DESC_TXT,formatter_class=RawFormatter)
    parser.add_argument("scenario", help="python file containing generation scenario")
    parser.add_argument("nbEntries", help="number of entries to generate (nb lines in the CSV file)")	
    parser.add_argument("targetFile", help="name of target file. If exists, append its contents")    
	parser.add_argument("--version", action="version", version="%(prog)s v"+VERSION)
    args = parser.parse_args()

    if not os.path.isfile(args.scenario):
        print("ERROR: scenario file not reachable : "+args.scenario)
        sys.exit(1)

    exec(open(args.scenario).read())
    
    if os.path.isfile(args.targetFile):
        print("appending file "+args.targetFile)
        f = open(args.targetFile, "a")
    else:
        print("creating file "+args.targetFile)
        f = open(args.targetFile, "w")
        # write header line
        line="#"
        for fieldName in fields:
            if len(line)>1:
                line+=";"
            line+=fieldName        
        f.write(line+"\n")
    
    for i in range(int(args.nbEntries)):    
        fieldsValues={}
        for fieldName in fields:            
            fieldsValues[fieldName]=fields[fieldName](i,int(args.nbEntries),fieldsValues)

        fieldIdx=0
        for fieldName in fieldsValues:
            if fieldIdx>0:
                f.write(";")
            f.write(str(fieldsValues[fieldName]))
            fieldIdx+=1            
        f.write("\n")
        if i%1000==0:
            print(" ... "+str(i))

    f.close()