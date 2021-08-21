#!/usr/local/bin/python3.8

import sys
import argparse
import os
import os.path

if __name__ == "__main__":
    
    # Define and parse arguments.
    parser = argparse.ArgumentParser()
    parser.add_argument("scenario", help="python file containing generation scenario")
    parser.add_argument("nbEntries", help="number of entries to generate")	
    parser.add_argument("targetFile", help="name of target file. If exists, append its contents")
    #parser.add_argument("--treshold_auto", default=0.9,help="score treshold ([0,1]) required for automatic acceptation of reconciliation result")
	
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