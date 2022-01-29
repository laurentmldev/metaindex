

#
# Download contents as CSV from a DTS access point
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

import shutil
import sys
import argparse
import os
import os.path
import requests
import re
import subprocess
import time
import json
from os.path import expanduser

import textwrap
from argparse import ArgumentParser, HelpFormatter


VERSION="2.1"

# convention for MetaindeX to read multi-line contents from CSV
CSV_CR_MARKER="&cr&"

SAXON_TRANSFORM_CLASS="net.sf.saxon.Transform"
TEI_TO_HTML5_XSL=os.sep+"html5"+os.sep+"html5.xsl"
TEI_TO_TXT_XSL=os.sep+"txt"+os.sep+"tei-to-text.xsl"

BREATH_TIME_SEC=0.1
RETRY_TIME_SEC=1

TARGET_PATH="./dts2csv_extract"

### from https://stackoverflow.com/questions/3853722/how-to-insert-newlines-on-argparse-help-text
class RawFormatter(HelpFormatter):
    def _fill_text(self, text, width, indent):
        return "\n".join([textwrap.fill(line, width) for line in textwrap.indent(textwrap.dedent(text), indent).splitlines()])

SAMPLE_CONFIG='''
{
    "SAXON_JAR_PATH":"HOME/dev/tei/install/SaxonHE10-5J/saxon-he-10.5.jar",
    "TEI_XSL_STYLESHEETS_PATH":"HOME/dev/tei/install/tei-xsl",
    "DTS_URL":"https://dts.perseids.org/",
    "DTS_COLLECTIONS_ENTRYPOINT":"collections",
    "DTS_DOCUMENTS_ENTRYPOINT":"documents",
    "START_COLLECTION_ID":"urn:perseids:latinLit",
    "MAX_DEPTH":"None",
    "RETRIEVE_FILES":"True",
    "TRANSFORM_TEI_TO_TXT":"True",
    "TRANSFORM_TEI_TO_HTML":"False",
    "INLINE_TXT_IN_CSV":"True",
    "COLLECTIONS": [
        {"dts_id":"totalItems","csv_name":"nbChildren", "mandatory":"True"},
        {"dts_id":"title"}
    ],
    "RESOURCES": [
        {"dts_id":"dts:dublincore/dc:language","csv_name":"language"},
        {"dts_id":"title"},
        {"dts_id":"description"}
    ]   
}
'''
DESC_TXT='''Extract data from DTS API and generate CSV files. 
If Saxon and proper Tei-Xsl files are provided, conversion from TEI files to Text and HTML is automated.
When conversion from TEI to text is done, basic text statistics are computed and added to generated CSV files.
    
Main input is a json config file defining expected info. Try '-sampleconf' option to display a sample.
'''

TEI_DOWNLOAD_NBTRIES=3

retrievedDtsCollections=[]
retrievedDtsResources=[]

def normalizeUrn(text):
    return str(text).replace(";",",").strip()

# transform given string into suitable CSV contents:
#   - if multiline, replace carriage returns by special string __CR__
#   - if CSV separator ';' is detected, then add bounds '"' characters 
#
def normalizeCsvContent(text):
    csvString=str(text).replace("\n",CSV_CR_MARKER).strip()
    if (';' in csvString):
            csvString='"'+csvString.replace('"','\\"')+'"'
    return csvString

def normalizeIdString(text):
    text=str(text)
    # remove trailing '/' in URL (potentially used for ID)
    if text.endswith("/"):
        text=text[:-1]
    return str(text).replace("https://","").replace("/","-").replace("urn:","").replace(":",".").strip()


# call Saxon and Tei-XSL stylesheets to convert TEI file in required format
def convertTei(conf,teiFile,stylesheet,outputFile):    
    os.system("java -cp "+ conf["SAXON_JAR_PATH"]+" "+SAXON_TRANSFORM_CLASS+" -xsl:"+conf["TEI_XSL_STYLESHEETS_PATH"]+stylesheet+" -s:"+teiFile+" -o:"+outputFile)

# retrieve DTS document object at given entrypoint/urn
# and store it in given file
def retrieveTeiFile(entrypoint,urn,targetFileName,nbtries=TEI_DOWNLOAD_NBTRIES):

    url=entrypoint+"?id="+urn

    # get json with information (including name and date) about Earth pictures
    response = requests.get(url,params = {"format": "text"} )
    
    if response.status_code != 200:
        print("WARNING: status code '"+str(response.status_code)+"' retrieved from given URL, expected was '200': "+url)
        if nbtries==0:
            raise ReferenceError("Unable to retrieve contents at URL '"+url+"' (status code:"+str(response.status_code)+"), aborting sorry.")
            

        time.sleep(RETRY_TIME_SEC*(TEI_DOWNLOAD_NBTRIES-nbtries+1))
        return retrieveTeiFile(entrypoint,urn,targetFileName,nbtries-1)

    elif nbtries<TEI_DOWNLOAD_NBTRIES:    
        print("Tried again, could finally retrieve it: "+url)
    
    # store data into local file
    try:
        targetfile= open(targetFileName, 'w')
        targetfile.write(response.text)
        targetfile.close()
        

    except Exception as e:
        raise SyntaxError("unable to write TEI contents from URL '"+url+"' into file '"+targetFileName+"' : "+str(e))
   

# retrieve DTS json object at given url/urn
# return python dictionary of retrieved json data
def retrieveDtsJsonContents(url,urn,nbtries=TEI_DOWNLOAD_NBTRIES):

    fullUrl=url
    if len(urn)>0:
        fullUrl+="?id="+urn

    
    # get json with information (including name and date) about Earth pictures
    response = requests.get(fullUrl,params = {"format": "json"} )
    if response.status_code != 200:
        print("WARNING: status code '"+str(response.status_code)+"' retrieved from given URL, expected was '200': "+fullUrl)
        if nbtries==0:
            raise ReferenceError("Unable to retrieve contents at URL '"+fullUrl+"' (status code:"+str(response.status_code)+"), aborting sorry.")            

        time.sleep(RETRY_TIME_SEC)
        return retrieveDtsJsonContents(url,urn,nbtries-1)

    elif nbtries<TEI_DOWNLOAD_NBTRIES:    
        print("Tried again, could finally retrieve it: "+fullUrl)
        
    # convert json to Python object 
    try:
        jsondata = response.json()
        return jsondata
    except Exception as e:
        raise SyntaxError("malformed 'json' contents retrieved from URL '"+fullUrl+"': "+str(e)+"\n-----\n"+response.text)
        

def findDtsAttrInList(attrDtsId, attrsList):
    for attrDesc in attrsList:
        if attrDesc["dts_id"]==attrDtsId:
            return attrDesc
    return None

def extractValueFromJson(jsonpath, jsondata, isMandatory):

    curDtsVal=jsondata        

    # if dts_id is a path (x/y/z), retrieve corresponding value within json contents
    attrPathList=jsonpath.split('/')        
    for key in attrPathList:            
        pos=None
        # if it's an array, retrieve the nth value (x/y[3]/z)
        m = re.match(r"(\S+)\[(\d+)\]",key)
        if m!=None:
            key=m.group(1)
            pos=int(m.group(2))
        
        if key not in curDtsVal:
            if isMandatory:
                raise AssertionError("given attribute not reachable: '"+jsonpath+"' (key '"+key+"' not defined in json contents)\n---\n"+str(curDtsVal))
            else:
                return None
        
        if pos!=None:
            curDtsVal=curDtsVal[key][pos]            
        else:
            curDtsVal=curDtsVal[key]

    return curDtsVal

# store contents of given DTS object and return list of subobjects to explore
def extractDtsJsonContents(conf,csvId,urn,jsondata,entrypoint,parentDtsObj,attrsList,depth):

    global retrievedDtsCollections
    global retrievedDtsResources
    
    if "@type" not in jsondata:
        raise print("Warning: contents retrieved from urn '"+urn+"' do not contain '@type' field, skipping it")
        return []

    sourceId=normalizeIdString(conf["DTS_URL"])
    startUrn=normalizeUrn(conf["START_COLLECTION_ID"])
    
    dtsObj={
            "dtstype":jsondata["@type"],
            "sourceid":sourceId,
            "startUrn":startUrn,
            "entrypoint":normalizeUrn(entrypoint),
            "urn":normalizeUrn(urn),
            "id":csvId,
                  
            "nbLines":0,
            "nbWords":0,
            "nbChars":0
        }

    if jsondata["@type"]=="Collection":
        print("".ljust(depth*2)+"[C] "+dtsObj['urn'],flush=True)
    elif jsondata["@type"]=="Resource":
        print("".ljust(depth*2)+"[R] "+dtsObj['urn'],flush=True)
    else:
        print("".ljust(depth*2)+"[?] "+dtsObj['urn'],flush=True)
        
    if "members" not in dtsObj:
            dtsObj["members"]=[]
    if "parent" not in dtsObj:
            dtsObj["parent"]=None

    if parentDtsObj != None:
        if "members" not in parentDtsObj:
            parentDtsObj["members"]=[]
        parentDtsObj["members"]+=[dtsObj]
        dtsObj["parent"]=parentDtsObj

    
    # extracting expected values from json 
    # following list defined in conf file
    for dtsAttrDesc in attrsList:
        
        dts_id=dtsAttrDesc["dts_id"]
        csv_name=dtsAttrDesc["csv_name"]
                
        isMandatory="mandatory" in dtsAttrDesc and dtsAttrDesc["mandatory"].lower()=="true"
        csvValue=extractValueFromJson(dts_id, jsondata, isMandatory)      
        if not csvValue:
            continue

        dtsObj[csv_name]=normalizeCsvContent(str(csvValue))       
    
    # store retrieved data in memory for later csv dump
    if dtsObj['dtstype']=="Collection":
        dtsObj["nbResources"]=0
        dtsObj["nbDirectResources"]=0
        retrievedDtsCollections+=[dtsObj]

    else:
        # set default value to 1 to allow generic counting statistics computation in aggregateCollectionsStats function
        dtsObj["nbResources"]=1
        retrievedDtsResources+=[dtsObj]

    # process children elements if any (and if MAX_DEPTH parameter allows it)
    if "member" in jsondata and (conf["MAX_DEPTH"]==None or depth<conf["MAX_DEPTH"]):
        for childDtsElementInfo in jsondata["member"]:
            processDtsElement(conf,childDtsElementInfo,entrypoint,dtsObj,depth+1)

    return dtsObj



def processCollectionDtsElement(conf,elementInfo,entrypoint,parentDtsObj,urn,csvId,depth=0):
    jsondata=retrieveDtsJsonContents(entrypoint,urn)
    if jsondata!=None:
        extractDtsJsonContents(conf,csvId,urn,jsondata,entrypoint,parentDtsObj,conf["COLLECTIONS"],depth)
    else:
        extractDtsJsonContents(conf,csvId,urn,elementInfo,entrypoint,parentDtsObj,conf["COLLECTIONS"],depth)

def processResourceDtsElement(conf,elementInfo,entrypoint,parentDtsObj,urn,csvId,depth=0):
    
    dtsObj=extractDtsJsonContents(conf,csvId,urn,elementInfo,entrypoint,parentDtsObj,conf["RESOURCES"],depth)

    if conf["RETRIEVE_FILES"]==True:

        os.makedirs(conf["TARGET_PATH"]+os.sep+"files",exist_ok=True)
        fileBaseName=conf["TARGET_PATH"]+os.sep+"files"+os.sep+normalizeUrn(csvId)
        teiFileName=fileBaseName+".xml"
        
        print("downloading "+teiFileName)
        retrieveTeiFile(conf["DTS_URL"]+"/"+conf["DTS_DOCUMENTS_ENTRYPOINT"],urn,teiFileName)     

        # convert TEI to plain text and HTML (if required in conf)
        targetConvertedFile=None
        try:
            if conf["TRANSFORM_TEI_TO_TXT"]:
                targetConvertedFile=fileBaseName+".txt"
                print("generating  "+targetConvertedFile+" with XSL "+conf["TEI_XSL_STYLESHEETS_PATH"]+TEI_TO_TXT_XSL)                
                convertTei(conf,teiFileName,TEI_TO_TXT_XSL,targetConvertedFile)
                dtsObj["textFile"]=targetConvertedFile

            if conf["TRANSFORM_TEI_TO_HTML"]:
                targetConvertedFile=fileBaseName+".html"
                print("generating  "+targetConvertedFile+" with XSL "+conf["TEI_XSL_STYLESHEETS_PATH"]+TEI_TO_HTML5_XSL)                
                convertTei(conf,teiFileName,TEI_TO_HTML5_XSL,targetConvertedFile)
                dtsObj["htmlFile"]=targetConvertedFile

        except Exception as e:
            print("ERROR: unable to convert TEI file: "+targetConvertedFile)
            print("ERROR: message was: "+str(e))
            # continue anyway     

def processDtsElement(conf,elementInfo,entrypoint,parentDtsObj,depth=0):

    urn = elementInfo["@id"]
    csvId = normalizeIdString(conf["DTS_URL"])+"_"+normalizeIdString(elementInfo["@id"])

    time.sleep(BREATH_TIME_SEC)
    
    if elementInfo["@type"]=="Collection":
        processCollectionDtsElement(conf,elementInfo,entrypoint,parentDtsObj,urn,csvId,depth)
        
    elif elementInfo["@type"]=="Resource":
        processResourceDtsElement(conf,elementInfo,entrypoint,parentDtsObj,urn,csvId,depth)
        
    else:
        return


# return tuple: nbLines, nbWords, nbChars, csvLine
def computeTextSatistics(textFile,withInlineCsv):

    stats={
        'nbWords':0,
        'nbLines':0,
        'nbChars':0
    }
    # used when option for inline contents in CSV has been required by user
    globalCsvLine=""

    try:
        with open(textFile) as f:
            for line in f:
                stats['nbChars']+=len(line)
                stats['nbWords']+=len(line.split())
                stats['nbLines']+=1
                if withInlineCsv==True:
                    globalCsvLine+=line
    
    except Exception as e:
        print("ERROR: unable to open file '"+str(textFile)+"': "+str(e))
        print("ERROR: skipping statistics for file '"+str(textFile)+"'")
        globalCsvLine="***File Not Found***"
            
    # put CSV inline contents into quotes
    if len(globalCsvLine)>0:
        globalCsvLine=normalizeCsvContent(globalCsvLine)        
    return stats,globalCsvLine
    

def aggregateCollectionsStats(conf,itemsList,retrievedCollections):

    nextCollectionsList=[]
    for itemData in itemsList:
        parentCollectionData=itemData["parent"]
        if parentCollectionData==None:
            continue
        
        parentCollectionData["nbResources"]+=itemData["nbResources"]
        
        if itemData["dtstype"]=="Resource":
            parentCollectionData["nbDirectResources"]+=1

        # if TEI transformed to text, we compiled some stats that we will propagate through parent collections
        if conf["TRANSFORM_TEI_TO_TXT"]:    
            parentCollectionData["nbLines"]+=itemData["nbLines"]
            parentCollectionData["nbWords"]+=itemData["nbWords"]
            parentCollectionData["nbChars"]+=itemData["nbChars"]

        nextCollectionsList+=[parentCollectionData]

    # hopefully there is no cyclic dependencies
    if len(nextCollectionsList)>0:
        aggregateCollectionsStats(conf,nextCollectionsList,retrievedCollections)


# build CSV Collections file
def dumpCollectionsCsv(conf,targetFileName,objsList,attrsList):
    global retrievedDtsCollections

    targetfile= open(targetFileName, 'w')

    headerLine="#id;dtstype;sourceId;entrypoint;urn;url;parent;members;nbMembers;nbResources;nbDirectResources"   

    if conf["TRANSFORM_TEI_TO_TXT"]:
         headerLine+=";nbLines;nbWords;nbChars"

    for attrDesc in attrsList:
        headerLine+=";"+attrDesc["csv_name"]
    targetfile.write(headerLine+"\n")

    for dtsParsedData in objsList:
        
        # document id
        curLine=dtsParsedData["id"]

        # dts type (Resource or Collection)
        curLine+=";"+dtsParsedData["dtstype"]

        # source id (given by user in config file)
        curLine+=";"+dtsParsedData["sourceid"]

        # entrypoint
        curLine+=";"+dtsParsedData["entrypoint"]
        
        # urn
        curLine+=";"+dtsParsedData["urn"]
        
        # url
        curLine+=";"+dtsParsedData["entrypoint"]+"?id="+dtsParsedData["urn"]

        # parent link
        curLine+=";"
        if dtsParsedData["parent"]!=None:
            curLine+=dtsParsedData["parent"]["id"]        

       # memberslink        
        membersStrList=""
        for memberObj in dtsParsedData["members"]:
            if len(membersStrList)>0:
                membersStrList+=","
            membersStrList+=memberObj["id"]
        curLine+=";"+membersStrList
        
        # nbMembers
        curLine+=";"+str(len(dtsParsedData["members"]))

        # nbResources
        curLine+=";"+str(dtsParsedData["nbResources"])

        # nbContainedResources
        curLine+=";"+str(dtsParsedData["nbDirectResources"])

        if conf["TRANSFORM_TEI_TO_TXT"]:                       
            curLine+=";"+str(dtsParsedData['nbLines'])
            curLine+=";"+str(dtsParsedData['nbWords'])
            curLine+=";"+str(dtsParsedData['nbChars'])

        # custom fields
        for attrDesc in attrsList:
            curLine+=";"
            csvName=attrDesc["csv_name"]
            if csvName in dtsParsedData:
                curLine+=dtsParsedData[csvName]

        targetfile.write(curLine+"\n")

    targetfile.close()


# build CSV Resources file
# return statistics summary, by unique ID
def dumpResourcesCsv(conf,targetFileName,objsList,attrsList):
    global retrievedDtsCollections

    targetfile= open(targetFileName, 'w')

    headerLine="#id;dtstype;sourceId;entrypoint;urn;url;parent"   
    if conf["TRANSFORM_TEI_TO_TXT"]:
         headerLine+=";textFilePath;textFileName;nbLines;nbWords;nbChars"
         
    if conf["TRANSFORM_TEI_TO_HTML"]:
         headerLine+=";htmlFilePath;htmlFileName"

    for attrDesc in attrsList:
        headerLine+=";"+attrDesc["csv_name"]

    if conf["INLINE_TXT_IN_CSV"]==True:
        headerLine+=";text"

    targetfile.write(headerLine+"\n")

    for dtsParsedData in objsList:
        # document id
        curLine=dtsParsedData["id"]

        # dts type (Resource or Collection)
        curLine+=";"+dtsParsedData["dtstype"]

        # source id (given by user in config file)
        curLine+=";"+dtsParsedData["sourceid"]

        # entrypoint
        curLine+=";"+dtsParsedData["entrypoint"]
        
        # urn
        curLine+=";"+dtsParsedData["urn"]
        
        # url
        curLine+=";"+dtsParsedData["entrypoint"]+"?id="+dtsParsedData["urn"]

        # parent link
        curLine+=";"
        if dtsParsedData["parent"]!=None:
            curLine+=dtsParsedData["parent"]["id"]        

        # text file name and path
        inlineContentsAsCsv="" # will be used for inline contents in CSV (if option activated by user in its config file)
        if conf["TRANSFORM_TEI_TO_TXT"]:
            curLine+=";"
            if "textFile" in dtsParsedData: # file path
                curLine+=dtsParsedData["textFile"]
            curLine+=";"
            if "textFile" in dtsParsedData: # file name
                curLine+=os.path.basename(dtsParsedData["textFile"])
            # text Stats
            textStats,inlineContentsAsCsv = computeTextSatistics(dtsParsedData["textFile"],withInlineCsv=conf["INLINE_TXT_IN_CSV"])
            dtsParsedData['nbLines']=textStats['nbLines']
            dtsParsedData['nbWords']=textStats['nbWords']
            dtsParsedData['nbChars']=textStats['nbChars']            
            curLine+=";"+str(dtsParsedData['nbLines'])
            curLine+=";"+str(dtsParsedData['nbWords'])
            curLine+=";"+str(dtsParsedData['nbChars'])

        # html file name and path
        if conf["TRANSFORM_TEI_TO_HTML"]:
            curLine+=";"
            if "htmlFile" in dtsParsedData: # file path
                curLine+=dtsParsedData["htmlFile"]
            curLine+=";"
            if "htmlFile" in dtsParsedData: # file name
                curLine+=os.path.basename(dtsParsedData["htmlFile"])

        # custom fields
        for attrDesc in attrsList:
            curLine+=";"
            csvName=attrDesc["csv_name"]
            if csvName in dtsParsedData:
                curLine+=dtsParsedData[csvName]

        # inline plain text
        if conf["INLINE_TXT_IN_CSV"]==True:
            curLine+=";"
            if "textFile" in dtsParsedData: # file path            
                # already loaded during call to computeTextSatistics up there
                curLine+=inlineContentsAsCsv

        targetfile.write(curLine+"\n")

    targetfile.close()
    
def loadConfig(fileName):

    home = expanduser("~")

    jsonFile=open(args.configfile)
    jsonConf=json.load(jsonFile)
    
    if jsonConf["MAX_DEPTH"]=="None":
        jsonConf["MAX_DEPTH"]=None
    else:
        jsonConf["MAX_DEPTH"]=int(jsonConf["MAX_DEPTH"])

    jsonConf["RETRIEVE_FILES"]=jsonConf["RETRIEVE_FILES"].lower()=="true"
    jsonConf["TRANSFORM_TEI_TO_TXT"]=jsonConf["TRANSFORM_TEI_TO_TXT"].lower()=="true"
    jsonConf["TRANSFORM_TEI_TO_HTML"]=jsonConf["TRANSFORM_TEI_TO_HTML"].lower()=="true"
    jsonConf["INLINE_TXT_IN_CSV"]=jsonConf["INLINE_TXT_IN_CSV"].lower()=="true"
    jsonConf["SAXON_JAR_PATH"]=jsonConf["SAXON_JAR_PATH"].replace("HOME",home)
    jsonConf["TEI_XSL_STYLESHEETS_PATH"]=jsonConf["TEI_XSL_STYLESHEETS_PATH"].replace("HOME",home)

    for attrDesc in jsonConf["COLLECTIONS"]:
        if len(attrDesc["dts_id"])==0:
            raise AssertionError("one of collections dts_id is empty:" + str(attrDesc)+". Aborting, sorry." )
        if "csv_name" not in attrDesc:
            attrDesc["csv_name"]=attrDesc["dts_id"]
    for attrDesc in jsonConf["RESOURCES"]:
        if len(attrDesc["dts_id"])==0:
            raise AssertionError("one of resources dts_id is empty:" + str(attrDesc)+". Aborting, sorry." )
        if "csv_name" not in attrDesc:
            attrDesc["csv_name"]=attrDesc["dts_id"]

    return jsonConf        

def checkConfConsistency(conf):
    
    # check conf consistency

    if not os.path.isfile(conf["TARGET_PATH"]):
        try:
            os.makedirs(conf["TARGET_PATH"],exist_ok=True)
        except Exception as e:
            raise FileNotFoundError("unable to create target folder '"+str(conf["TARGET_PATH"])+"': "+str(e))
    
    if (conf["TRANSFORM_TEI_TO_TXT"]==True or conf["TRANSFORM_TEI_TO_HTML"]==True) and not conf["RETRIEVE_FILES"]==True:
        raise AssertionError("from your config file, RETRIEVE_FILES="+str(conf["RETRIEVE_FILES"])+" while it must be True if you want to use option TRANSFORM_TEI_TO_TXT ("\
                                                        +str(conf["TRANSFORM_TEI_TO_TXT"])+" in your config) or TRANSFORM_TEI_TO_HTML ("+str(conf["TRANSFORM_TEI_TO_HTML"])+" in your config)")
        

    if (conf["TRANSFORM_TEI_TO_TXT"]==True or conf["TRANSFORM_TEI_TO_HTML"]==True) and not os.path.isfile(conf["SAXON_JAR_PATH"]):
        raise AssertionError("given SAXON jar file path is not reachable: SAXON_JAR_PATH=\""+str(conf["SAXON_JAR_PATH"])+"\"")

    if conf["TRANSFORM_TEI_TO_TXT"]==True and not os.path.isfile(conf["TEI_XSL_STYLESHEETS_PATH"]+TEI_TO_TXT_XSL):
        raise AssertionError("ERROR: given Tei-XSL stylesheets path is not reachable or does not contain expected files. Was expecting file \""+str(conf["TEI_XSL_STYLESHEETS_PATH"]+TEI_TO_TXT_XSL)+"\"")

    if conf["TRANSFORM_TEI_TO_HTML"]==True and not os.path.isfile(conf["TEI_XSL_STYLESHEETS_PATH"]+TEI_TO_HTML5_XSL):
        raise AssertionError("ERROR: given Tei-XSL stylesheets path is not reachable or does not contain expected files. Was expecting file \""+str(conf["TEI_XSL_STYLESHEETS_PATH"]+TEI_TO_HTML5_XSL)+"\"")

    if conf["INLINE_TXT_IN_CSV"]==True and not conf["TRANSFORM_TEI_TO_TXT"]==True:
        raise AssertionError("ERROR: from your config file, TRANSFORM_TEI_TO_TXT="+str(conf["TRANSFORM_TEI_TO_TXT"])+" while it must be True if you want to use option INLINE_TXT_IN_CSV ("\
                                                        +str(conf["INLINE_TXT_IN_CSV"])+" in your config)")

def extract_all(conf, zipResult=False, targetFolder=TARGET_PATH):

    conf["TARGET_PATH"]=targetFolder
    checkConfConsistency(conf)
    
    rootElementInfo={"@id":conf["START_COLLECTION_ID"], "@type":"Collection"}
    collectionsEntryUrl=conf["DTS_URL"]+"/"+conf["DTS_COLLECTIONS_ENTRYPOINT"]
    
    processDtsElement(conf,rootElementInfo,collectionsEntryUrl,None)

    dumpResourcesCsv(conf,conf["TARGET_PATH"]+os.sep+"resources.csv",retrievedDtsResources,conf["RESOURCES"])
    aggregateCollectionsStats(conf,retrievedDtsResources,retrievedDtsCollections)
    dumpCollectionsCsv(conf,conf["TARGET_PATH"]+os.sep+"collections.csv",retrievedDtsCollections,conf["COLLECTIONS"])    
    
    if zipResult==True:
        zipName=os.path.basename(conf["TARGET_PATH"])
        if len(zipName)==0 or zipName=="." or zipName=="..":
            zipName="dts2csv_extract"
        zipName=conf["TARGET_PATH"]+os.sep+".."+os.sep+zipName
        shutil.make_archive(zipName, "zip", conf["TARGET_PATH"])
        print("Finished extraction, files generated in '"+os.path.abspath(zipName+".zip")+"', bye bye.")
        return os.path.abspath(zipName+".zip")
    else:
        print("Finished extraction, files generated in '"+os.path.abspath(conf["TARGET_PATH"])+"', bye bye.")
        return os.path.abspath(conf["TARGET_PATH"])

    
    
if __name__ == "__main__":

    # Define and parse arguments.
    parser = argparse.ArgumentParser(prog="MetaindeX Toolbox - "+__file__+" (from http://metaindex.fr)",description=DESC_TXT,formatter_class=RawFormatter)
    parser.add_argument("configfile",nargs='?',default="",help="python file containing configuration data (see full example in description text up there).")
    parser.add_argument("-sampleconf", action="store_true", help="display a sample config file.")
    parser.add_argument("-o", nargs='?',default="./dts2csv_extract", help="folder where to store resulting data")
    parser.add_argument("-z", action="store_true", help="compress result as a zip file")
    parser.add_argument("--version", action="version", version="%(prog)s v"+VERSION)
    args = parser.parse_args()

    if args.sampleconf==True:
        print(SAMPLE_CONFIG)
        sys.exit(0)

    if len(args.configfile)==0:
        print("ERROR: missing input argument: configfile")
        sys.exit(1)


    if not os.path.isfile(args.configfile):
        print("ERROR: given config file not reachable : '"+confJson+"'")
        sys.exit(1)

    targetPath=TARGET_PATH
    if len(args.o)!=0:
        targetPath=args.o
    
    try:
        confJson=loadConfig(args.configfile)
        generatedFolder= extract_all(confJson,args.z,targetPath)
    except Exception as e:
        print("ERROR: "+str(e))

    sys.exit(0)
    
