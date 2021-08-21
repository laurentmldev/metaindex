
/*

Author: Laurent ML - metaindex.fr 2021
If you find this tools useful somehow, please reference MetaindeX project when possible.


GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import sys
import argparse
import os
import os.path
import requests
import re
import subprocess
import time

import textwrap
from argparse import ArgumentParser, HelpFormatter

# convention for MetaindeX to read multi-line contents from CSV
MX_CSV_CR_MARKER="__CR__"
MX_ESCAPED_SEPARATOR="__MX_ESCAPED_SEPARATOR__"

SAXON_TRANSFORM_CLASS="net.sf.saxon.Transform"
TEI_TO_HTML5_XSL=os.sep+"html5"+os.sep+"html5.xsl"
TEI_TO_TXT_XSL=os.sep+"txt"+os.sep+"tei-to-text.xsl"

BREATH_TIME_SEC=0.1
RETRY_TIME_SEC=1

### from https://stackoverflow.com/questions/3853722/how-to-insert-newlines-on-argparse-help-text
class RawFormatter(HelpFormatter):
    def _fill_text(self, text, width, indent):
        return "\n".join([textwrap.fill(line, width) for line in textwrap.indent(textwrap.dedent(text), indent).splitlines()])

DESC_TXT='''Extract data from DTS API and generate CSV files. 
If Saxon and proper Tei-Xsl files are provided, conversion from TEI files to Text and HTML is automated.
When conversion from TEI to text is done, basic text statistics are computed and added to generated CSV files.
    
Main input is a python file with following config info (given example is based on perseids data sample):

# ----- start of Python config file example -----

# ----- DTS API config -----
# API URLs configuration.
# Such info is usually available when accessing the DTS API entrypoint of the server.
apiEntrypoint="https://dts.perseids.org/"
COLLECTIONS_URL=apiEntrypoint+"collections"
DOCUMENTS_URL=apiEntrypoint+"documents"
NAVIGATION_URL=apiEntrypoint+"navigation"

# Where to start from, generally set to 'default' to start from root
subsetId="farsi"
ROOT_COLLECTION_ID="urn:perseids:"+subsetId+"Lit"

# how deep (integer) to go into collections tree.
# set to None for no max depth
MAX_DEPTH=None

# ----- TEI download config -----
# set True to download TEI files, otherwise only get info from DTS API
RETRIEVE_FILES=True 

# set True to convert TEI to text files (needs Saxon and Tei-Xsl, see deps below)
TRANSFORM_TEI_TO_TXT=True 

# set True to convert TEI to html files (needs Saxon and Tei-Xsl, see deps below)
TRANSFORM_TEI_TO_HTML=True 

# inject plain text contents into (resources) CSV file
# Set True to put TEI plain-text contents into generated CSV files, so that it can be loaded for example into MetaindeX
# needs TRANSFORM_TEI_TO_TXT=True
INLINE_TXT_IN_CSV=True 

# dependencies for TEI conversion (only needed if TEI transform to text or html is required)
## Path to SAXON XSLT engine
SAXON_JAR_PATH="/path/to/saxon-he-10.5.jar" 

## Path to TEI-XSL Stylesheets
## shall contain at least following files:
## <TEI_XSL_STYLESHEETS_PATH>'''+TEI_TO_HTML5_XSL+'''
## <TEI_XSL_STYLESHEETS_PATH>'''+TEI_TO_TXT_XSL+'''
TEI_XSL_STYLESHEETS_PATH="/path/to/tei-xsl"

# ----- CSV conversion config -----
# some common dataset id to help identifying entries in CSV contents
DATASET_ID="perseids"

# where to store generated files
TARGET_PATH="/path/to/results/dts/perseids/"+subsetId

# OPTIONAL FUNCTION
# called every time we try to download a TEI file
# if this function returns False we will skip the file
# it it returns True we will actually download it.
#
# if this function is missing, it is assumed to be always True
def config_filterResource(resourceCsvData,resourceJsonData):
    return True # retrieve all TEI files
    #return resourceCsvData["language"]=="en" # retrieve on TEI files marked as english language

# OPTIONAL FUNCTION
# CSV ids might have some unicity or syntaxic constraints which
# can be handled here. Default behaviour is to remove 'urn' text and replace ':' by '.'
# to be compatible with Lucene query syntax (used by MetaindeX)
#def config_idDts2idCsv(dtsId,dtsJsonData):
#    return dtsId.replace('urn:','').replace(':','.').replace('urn.','')

# local function to normalize a given string for CSV contents, avoiding
# syntaxic conflics regarding newlines and CSV separator
# "'''+MX_CSV_CR_MARKER+'''" is transcoded to newline by MetaindeX during import
# "'''+MX_ESCAPED_SEPARATOR+'''" is transcoded to ';' by MetaindeX during import
def normalizeText(text):
    return text.replace("\\n","'''+MX_CSV_CR_MARKER+'''").replace("  "," ").replace(";","'''+MX_ESCAPED_SEPARATOR+'''")

# Same basic fields are automatically extracted: 'id','type','url', 'urn', 'members' and 'parent'. 
# Others fields shall be listed hereunder <dts-path> : <csv-id>
#
# dts-path is a basic version of xpath, using '/' for children, and 'xxx[i]' for arrays.
# For example 'dts:extensions/cts:description[0]/@value' is a valid expression.
#
# allowed options for CSV conversion are:
#  - 'csvName' (string): name of corresponding CSV column
#  - 'mandatory' (bool): if True, raise an error if field not found in DTS json data, if False, ignore it silently. Default is False.
#  - 'transform' (func): a callback function to be invoked when storing DTS json as CSV contents. Default is 'keep string as is'
ATTRS_LIST={
    "Collection" :{ "totalItems" : {"csvName":"nbChildren", "mandatory":True}, 
                    "title" : {"csvName":"title", "mandatory":True, "transform":normalizeText},                            
                },
    
    
    "Resource":{ 
                "title" : {"csvName":"title", "mandatory":True, "transform":normalizeText},
                "description" : {"csvName":"description", "mandatory":True, "transform":normalizeText},
                "dts:dublincore/dc:language" : {"csvName":"language", "mandatory":True},
            }
}
# ----- end of Python config file example -----
    '''


TEI_DOWNLOAD_NBTRIES=3

retrievedDtsCollections=[]
retrievedDtsResources=[]

# -----------------------------
# To be defined in user config file
RETRIEVE_FILES=False
TRANSFORM_TEI_TO_TXT=False
TRANSFORM_TEI_TO_HTML=False
INLINE_TXT_IN_CSV=False

TARGET_PATH=None
MAX_DEPTH=None

COLLECTIONS_URL=None
DOCUMENTS_URL=None
NAVIGATION_URL=None

SAXON_JAR_PATH=None
TEI_XSL_STYLESHEETS_PATH=None
# -----------------------------



def removeSemicolumns(text):
    return str(text).replace(";",",").strip()

# call Saxon and Tei-XSL stylesheets to convert TEI file in required format
def convertTei(teiFile,stylesheet,outputFile):
    #return subprocess.call("java", "-cp", SAXON_JAR_PATH,SAXON_TRANSFORM_CLASS,"-xsl:"+stylesheet,"-s:"+teiFile,"-o:"+outputFile)
    os.system("java -cp "+ SAXON_JAR_PATH+" "+SAXON_TRANSFORM_CLASS+" -xsl:"+stylesheet+" -s:"+teiFile+" -o:"+outputFile)

# retrieve DTS document object at given entrypoint/urn
# and store it in given file
def retrieveTeiFile(entrypoint,urn,targetFileName,nbtries=TEI_DOWNLOAD_NBTRIES):

    url=entrypoint+"?id="+urn

    # get json with information (including name and date) about Earth pictures
    response = requests.get(url,params = {"format": "text"} )
    if response.status_code != 200:
        print("WARNING: status code '"+str(response.status_code)+"' retrieved from given URL, expected was '200': "+url)
        if nbtries==0:
            print("I tried enough, aborting sorry.")
            sys.exit(1)

        time.sleep(RETRY_TIME_SEC)
        return retrieveTeiFile(entrypoint,urn,targetFileName,nbtries-1)

    elif nbtries<TEI_DOWNLOAD_NBTRIES:    
        print("Tried again, could finally retrieve it: "+url)
        
    # store data into local file
    try:
        targetfile= open(targetFileName, 'w')
        targetfile.write(response.text)
        targetfile.close()

    except Exception as e:
        print("ERROR: unable to decode as 'json' contents retrieved from URL '"+url+"': "+str(e))
        print(response.text)
        sys.exit(1)
   

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
            print("I tried enough, aborting sorry.")
            sys.exit(1)

        time.sleep(RETRY_TIME_SEC)
        return retrieveDtsJsonContents(url,urn,nbtries-1)

    elif nbtries<TEI_DOWNLOAD_NBTRIES:    
        print("Tried again, could finally retrieve it: "+fullUrl)
    
    # convert json to Python object 
    try:
        jsondata = response.json()
        return jsondata
    except Exception as e:
        print("ERROR: unable to decode as 'json' contents retrieved from URL '"+fullUrl+"': "+str(e))
        print(response.text)
        sys.exit(1)

# store contents of given DTS object and return list of subobjects to explore
def extractDtsJsonContents(sourceid,csvId,urn,jsondata,entrypoint,parentDtsObj,attrsList,depth):

    global retrievedDtsCollections
    global retrievedDtsResources
    
    if "@type" not in jsondata:
        print("ERROR: contents retrieved from urn '"+urn+"' do not contain '@type' field, skipping it")
        return []

    dtsObj={
            "sourceid":sourceid,
            "dtstype":jsondata["@type"],
            "entrypoint":removeSemicolumns(entrypoint),
            "urn":removeSemicolumns(urn),
            "id":csvId,      
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

    if jsondata["@type"] not in attrsList:
        print("ERROR: not attributes list found for DTS element type '"+jsondata["@type"]+"', available types in given config are: ")
        print(attrsList.keys())
        sys.exit(1)

    # extracting attributes listed in user config file
    for dtsAttrName in attrsList[jsondata["@type"]].keys():
        attrFound=True
        csvAttrDesc=attrsList[jsondata["@type"]][dtsAttrName]
        attrPathList=dtsAttrName.split('/')
        curDtsVal=jsondata        
        for key in attrPathList:            
            pos=None
            m = re.match(r"(\S+)\[(\d+)\]",key)
            if m!=None:
                key=m.group(1)
                pos=int(m.group(2))

            if key not in curDtsVal:
                if "mandatory" in csvAttrDesc and csvAttrDesc["mandatory"]==True:
                    print("ERROR: given attribute path not reachable: '"+dtsAttrName+"' (key '"+key+"' not defined in contents: ")
                    print(curDtsVal)
                    sys.exit(1)
                else:
                    attrFound=False
                    break
            
            if pos!=None:
                curDtsVal=curDtsVal[key][pos]            
            else:
                curDtsVal=curDtsVal[key]
                
        if not attrFound:
            continue

        if "transform" in attrsList[jsondata["@type"]][dtsAttrName]:
                    curDtsVal=attrsList[jsondata["@type"]][dtsAttrName]["transform"](curDtsVal)        

        dtsObj[csvAttrDesc['csvName']]=str(curDtsVal)
    
    # store retrieved data in memory for later csv dump
    if dtsObj['dtstype']=="Collection":
        retrievedDtsCollections+=[dtsObj]
    else:
        retrievedDtsResources+=[dtsObj]

    # process children elements if any (and if MAX_DEPTH parameter allows it)
    if "member" in jsondata and (MAX_DEPTH==None or depth<MAX_DEPTH):
        for childDtsElementInfo in jsondata["member"]:
            processDtsElement(sourceid,childDtsElementInfo,entrypoint,dtsObj,attrsList,depth+1)

    return dtsObj


# default implementation, can be overriden by function 'config_idDts2idCsv' in user config file
def defaultIdDts2idCsv(idDts):
    return idDts.replace('urn:','').replace(':','.').replace('urn.','')


def processDtsElement(sourceid,elementInfo,entrypoint,parentDtsObj,attrsList,depth=0):

    urn = elementInfo["@id"]
    csvId = defaultIdDts2idCsv(elementInfo["@id"])

    try:
        csvId=config_idDts2idCsv(elementInfo["@id"],elementInfo)
    except NameError:
        pass
    except Exception as e:
        print("ERROR: while running custom function 'config_idDts2idCsv': "+str(e))
        sys.exit(1)

    time.sleep(BREATH_TIME_SEC)
    
    if elementInfo["@type"]=="Collection":
        jsondata=retrieveDtsJsonContents(entrypoint,urn)
        if jsondata!=None:
            extractDtsJsonContents(sourceid,csvId,urn,jsondata,entrypoint,parentDtsObj,attrsList,depth)
        else:
            extractDtsJsonContents(sourceid,csvId,urn,elementInfo,entrypoint,parentDtsObj,attrsList,depth)
        
    elif elementInfo["@type"]=="Resource":
        dtsObj=extractDtsJsonContents(sourceid,csvId,urn,elementInfo,entrypoint,parentDtsObj,attrsList,depth)
        if RETRIEVE_FILES==True:

            # apply custom filter if defined
            try:
                if not config_filterResource(dtsObj,elementInfo):
                    return
            except NameError:
                pass
            except Exception as e:
                print("ERROR: while running custom function 'config_filterResource': "+str(e))
                sys.exit(1)

            os.makedirs(TARGET_PATH+os.sep+"files",exist_ok=True)
            fileBaseName=TARGET_PATH+os.sep+"files"+os.sep+removeSemicolumns(csvId)
            teiFileName=fileBaseName+".xml"
            
            print("downloading "+teiFileName)
            retrieveTeiFile(DOCUMENTS_URL,urn,teiFileName)     

            # convert TEI to plain text
            try:
                if TRANSFORM_TEI_TO_TXT:
                    txtFileName=fileBaseName+".txt"
                    print("generating  "+txtFileName+" with XSL "+TEI_XSL_STYLESHEETS_PATH+TEI_TO_TXT_XSL)                
                    convertTei(teiFileName,TEI_XSL_STYLESHEETS_PATH+TEI_TO_TXT_XSL,txtFileName)
                    dtsObj["textFile"]=txtFileName

                if TRANSFORM_TEI_TO_HTML:
                    htmlFileName=fileBaseName+".html"
                    print("generating  "+htmlFileName+" with XSL "+TEI_XSL_STYLESHEETS_PATH+TEI_TO_HTML5_XSL)                
                    convertTei(teiFileName,TEI_XSL_STYLESHEETS_PATH+TEI_TO_HTML5_XSL,htmlFileName)
                    dtsObj["htmlFile"]=htmlFileName

            except Exception as e:
                print("ERROR: unable to convert TEI file: "+targetFileName)
                print("ERROR: message was: "+str(e))
                # continue anyway   
        
    else:
        return

def dumpCollectionsCsv(targetFileName,objsList,attrsList):
    global retrievedDtsCollections

    targetfile= open(targetFileName, 'w')

    headerLine="#id;dtstype;sourceId;entrypoint;urn;url;parent;members"    
    for attrName in attrsList.keys():
        headerLine+=";"+attrsList[attrName]["csvName"]
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
        
        # custom fields
        for attrName in attrsList.keys():
            curLine+=";"
            csvName=attrsList[attrName]["csvName"]
            if csvName in dtsParsedData:
                curLine+=dtsParsedData[csvName]

        targetfile.write(curLine+"\n")

    targetfile.close()


# return tuple: nbLines, nbWords, nbChars, csvLine
def computeTextSatistics(textFile,withInlineCsv):

    stats={
        'nbWords':0,
        'nbLines':0,
        'nbChars':0
    }
    # used when option for inline contents in CSV has been required by user
    globalCsvLine=""

    with open(textFile) as f:
        for line in f:
            stats['nbChars']+=len(line)
            stats['nbWords']+=len(line.split())
            stats['nbLines']+=1
            if withInlineCsv==True:
                globalCsvLine+=line.replace("\n",MX_CSV_CR_MARKER).replace(";",MX_ESCAPED_SEPARATOR)
    
    return stats,globalCsvLine
    

def dumpResourcesCsv(targetFileName,objsList,attrsList):
    global retrievedDtsCollections

    targetfile= open(targetFileName, 'w')

    headerLine="#id;dtstype;sourceId;entrypoint;urn;url;parent"   
    if TRANSFORM_TEI_TO_TXT:
         headerLine+=";textFilePath;textFileName;nbLines;nbWords;nbChars"
         
    if TRANSFORM_TEI_TO_HTML:
         headerLine+=";htmlFilePath;htmlFileName"

    for attrName in attrsList.keys():
        headerLine+=";"+attrsList[attrName]["csvName"]

    if INLINE_TXT_IN_CSV==True:
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
        if TRANSFORM_TEI_TO_TXT:
            curLine+=";"
            if "textFile" in dtsParsedData: # file path
                curLine+=dtsParsedData["textFile"]
            curLine+=";"
            if "textFile" in dtsParsedData: # file name
                curLine+=os.path.basename(dtsParsedData["textFile"])
            # text Stats
            textStats,inlineContentsAsCsv = computeTextSatistics(dtsParsedData["textFile"],withInlineCsv=INLINE_TXT_IN_CSV)
            curLine+=";"+str(textStats['nbLines'])
            curLine+=";"+str(textStats['nbWords'])
            curLine+=";"+str(textStats['nbChars'])

        # html file name and path
        if TRANSFORM_TEI_TO_HTML:
            curLine+=";"
            if "htmlFile" in dtsParsedData: # file path
                curLine+=dtsParsedData["htmlFile"]
            curLine+=";"
            if "htmlFile" in dtsParsedData: # file name
                curLine+=os.path.basename(dtsParsedData["htmlFile"])

        # custom fields
        for attrName in attrsList.keys():
            curLine+=";"
            csvName=attrsList[attrName]["csvName"]
            if csvName in dtsParsedData:
                curLine+=dtsParsedData[csvName]

        # inline plain text
        if INLINE_TXT_IN_CSV==True:
            curLine+=";"
            if "textFile" in dtsParsedData: # file path            
                # already loaded during call to computeTextSatistics up there
                curLine+=inlineContentsAsCsv

        targetfile.write(curLine+"\n")

    targetfile.close()

if __name__ == "__main__":

    # Define and parse arguments.
    parser = argparse.ArgumentParser(description=DESC_TXT,formatter_class=RawFormatter)
    parser.add_argument("configfile", help="python file containing configuration data (see full example in description text up there).")

    args = parser.parse_args()

    if not os.path.isfile(args.configfile):
        print("ERROR: config file not reachable : "+args.configfile)
        sys.exit(1)
    exec(open(args.configfile).read())

    if not os.path.isfile(TARGET_PATH):
        try:
            os.makedirs(TARGET_PATH,exist_ok=True)
        except Exception as e:
            print("ERROR: unable to create target folder '"+str(TARGET_PATH)+"': "+str(e))
            sys.exit(1)

    
    if (TRANSFORM_TEI_TO_TXT==True or TRANSFORM_TEI_TO_HTML==True) and not RETRIEVE_FILES==True:
        print("ERROR: from your config file, RETRIEVE_FILES=="+str(RETRIEVE_FILES)+" while it must be True if you want to use option TRANSFORM_TEI_TO_TXT ("\
                                                        +str(TRANSFORM_TEI_TO_TXT)+" in your config) or  TRANSFORM_TEI_TO_HTML ("+str(TRANSFORM_TEI_TO_HTML)+" in your config)")
        sys.exit(1)

    if (TRANSFORM_TEI_TO_TXT==True or TRANSFORM_TEI_TO_HTML==True) and not os.path.isfile(SAXON_JAR_PATH):
        print("ERROR: given SAXON jar file path is not reachable: SAXON_JAR_PATH=\""+str(SAXON_JAR_PATH)+"\"")
        sys.exit(1)

    if TRANSFORM_TEI_TO_TXT==True and not os.path.isfile(TEI_XSL_STYLESHEETS_PATH+TEI_TO_TXT_XSL):
        print("ERROR: given Tei-XSL stylesheets path is not reachable or does not contain expected files. Was expecting file \""+str(TEI_XSL_STYLESHEETS_PATH+TEI_TO_TXT_XSL)+"\"")
        sys.exit(1)
    if TRANSFORM_TEI_TO_HTML==True and not os.path.isfile(TEI_XSL_STYLESHEETS_PATH+TEI_TO_HTML5_XSL):
        print("ERROR: given Tei-XSL stylesheets path is not reachable or does not contain expected files. Was expecting file \""+str(TEI_XSL_STYLESHEETS_PATH+TEI_TO_HTML5_XSL)+"\"")
        sys.exit(1)

    if INLINE_TXT_IN_CSV==True and not TRANSFORM_TEI_TO_TXT==True:
        print("ERROR: from your config file, TRANSFORM_TEI_TO_TXT=="+str(TRANSFORM_TEI_TO_TXT)+" while it must be True if you want to use option INLINE_TXT_IN_CSV ("\
                                                        +str(INLINE_TXT_IN_CSV)+" in your config)")
        sys.exit(1)

    rootElementInfo={"@id":ROOT_COLLECTION_ID, "@type":"Collection"}
    processDtsElement(DATASET_ID,rootElementInfo,COLLECTIONS_URL,None,ATTRS_LIST)

    dumpCollectionsCsv(TARGET_PATH+os.sep+"collections.csv",retrievedDtsCollections,ATTRS_LIST["Collection"])
    dumpResourcesCsv(TARGET_PATH+os.sep+"resources.csv",retrievedDtsResources,ATTRS_LIST["Resource"])
    print("Files generated in '"+TARGET_PATH+"', you can now import them into metaindex.fr if you wish! bye bye.")
    
    #if os.path.isfolder(args.targetfolder):
    #    print("ERROR: given target folder already exists, please remove it or use -f option : "+args.targetfolder)
    #    sys.exit(1)

        
    
    