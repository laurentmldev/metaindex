

#
# Generate quiz questions from CSV file and json config
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

import sys
import argparse
import math
import os

import time
import json
import random
import shutil
from os.path import expanduser
from pathlib import Path


import textwrap
from argparse import ArgumentParser, HelpFormatter

random.seed(a=None)

QCM_DATAFILE_NAME="qcmbin"
QCM_SUMMARYFILE_NAME="qcmsum"
QCM_AUTHOR_ID=0
QCM_SUBJECT_ID=0
IMGPREFIX="res/pics/"

MAX_NB_TRIES_FOR_PROPOSAL=500
MAX_NB_TRIES_FOR_QUESTION=500

DESC_TXT='''Extract data from CSV file and generate a quiz following instructions given in JSON config file.
'''

previousPourcentProgress=0.0

### from https://stackoverflow.com/questions/3173320/text-progress-bar-in-terminal-with-block-characters
# Print iterations progress
def printProgressBar (iteration, total, prefix = '', suffix = 'Complete', decimals = 1, length = 100, fill = '█', printEnd = "\r",short=False):
    """
    Call in a loop to create terminal progress bar
    @params:
        iteration   - Required  : current iteration (Int)
        total       - Required  : total iterations (Int)
        prefix      - Optional  : prefix string (Str)
        suffix      - Optional  : suffix string (Str)
        decimals    - Optional  : positive number of decimals in percent complete (Int)
        length      - Optional  : character length of bar (Int)
        fill        - Optional  : bar fill character (Str)
        printEnd    - Optional  : end character (e.g. "\r", "\r\n") (Str)
    """
    #printEnd="\033[F" #A
    global previousPourcentProgress

    if iteration==0:
        previousPourcentProgress=0

    if total==0:
        return
    percent = ("{0:." + str(decimals) + "f}").format(100 * (iteration / float(total)))
    # 'short' mode used for minimal output to be interpreted by third-party tool
    if short==True:
        # avoid displaying too many progress messages
        if float(percent)-previousPourcentProgress>=1.0:
            print("PROGRESS: "+prefix+" "+str(percent)+"%",flush=True)
            previousPourcentProgress=float(percent)            
        return

    filledLength = int(length * iteration // total)
    bar = fill * filledLength + '-' * (length - filledLength)
    print(f'{prefix} |{bar}| {percent}% {suffix} ', end = printEnd)
    # Print New Line on Complete
    if iteration == total: 
        print("")

def printProgress (iteration, prefix = ''):
    progressChars=[ "-", "\\", "|", "/" ]
    curCharPos=iteration%len(progressChars)
    print(progressChars[curCharPos], end='\r')    

### from https://stackoverflow.com/questions/3853722/how-to-insert-newlines-on-argparse-help-text
class RawFormatter(HelpFormatter):
    def _fill_text(self, text, width, indent):
        return "\n".join([textwrap.fill(line, width) for line in textwrap.indent(textwrap.dedent(text), indent).splitlines()])


    
def loadJson(fileName):
    jsonFile=open(fileName)
    jsonConf=json.load(jsonFile)
    
    return jsonConf        

    
def saveDictToJson(quizData,fileName):
    with open(fileName,"w") as fp:
        json.dump(quizData,fp)

def isAPicture(valueStr):
    return valueStr.lower().endswith(".jpg") \
        or valueStr.lower().endswith(".png") \
        or valueStr.lower().endswith(".webp") \
        or valueStr.lower().endswith(".gif") \
        or valueStr.lower().endswith(".tif")

def propositionToQcmBin(fieldsList,proposalData,isAGoodAnswer,timestampStr):

    labelStr=""
    picFile=None
    idx=0
    for fieldStr in fieldsList:
        fieldValue=proposalData[fieldStr]
        
        if isAPicture(fieldValue):
            picFile=fieldValue                
        else:    
            idx=idx+1
            if idx>1:
                labelStr+=" "
            labelStr+=fieldValue

    propositionQcmData={
        "caseSensitive": False,
        "truth": isAGoodAnswer,
        "createdAt": timestampStr,
        "extras": {},
        "label": labelStr,
        "updatedAt": timestampStr,
        "uriMap": {}
    }

    if picFile!=None:
        propositionQcmData["uriMap"]={"images":picFile}
    
    return propositionQcmData

def quizToQcmBin(quizData,fileName,timestampStr="2022-06-20T07:56:07.869+0200"):
    qcmbinData={ "qcms": [] }
    questionID=0
    for question in quizData["questions"]:
        questionID+=1

        questionCommentStr=""
        for unusuedFieldName in question["unusedFieldsValues"].keys():
            if len(questionCommentStr)>0:
                questionCommentStr+=", "
            questionCommentStr+=unusuedFieldName+":"+str(question["unusedFieldsValues"][unusuedFieldName])

        # general info
        questionQcmInfo={
            "aurthorId":QCM_AUTHOR_ID,
            "subjectId":QCM_SUBJECT_ID,
            "type": "auto",
            "typeName": "auto",
            "comments": [{
                "createdAt": timestampStr,
                "extras": {},
                "label": questionCommentStr+"\n",
                "updatedAt": timestampStr,
                "uriMap": {}
            }],
            "extras": {},
            "id": questionID,
            "knowledgeLevelId": "en-21",
            "maxPropositionPerExercise": -1,
            "maxTruePropositionPerExercise": -1,
            "propositionRandomizationType": "ifNeeded",
            "propositions":[],
            "question": {}
        }

        # build question details
        picFile=None
        
        labelStr="For"
        presData=question["presFields"]
        idx=0
        for presFieldName in presData.keys():
            idx=idx+1
            if idx>1:
                labelStr+","
            fieldValue=presData[presFieldName]
            if isAPicture(fieldValue):
                picFile=fieldValue
                labelStr+=" this image"
            else:    
                if len(fieldValue)==0:
                    labelStr+=" empty "+presFieldName
                else:
                    labelStr+=" "+fieldValue+" ("+presFieldName+")"
        
        labelStr+="\nFind"
        answer=question["answer"]
        questionFieldsList=answer.keys()    
        idx=0
        for questionFieldName in questionFieldsList:
            idx=idx+1
            if idx>1:
                labelStr+","
            labelStr+=" "+questionFieldName
        

        questionQcmDesc={
            "comment": "",
            "createdAt": timestampStr,
            "extras": {},
            "label": labelStr+"\n",
            "updatedAt": timestampStr,
            "uriMap": {}
        }
        if picFile!=None:
            questionQcmDesc["uriMap"]={"images":picFile}

        questionQcmInfo["question"]=questionQcmDesc

        # build propositions details
        questionQcmInfo["propositions"]+=[propositionToQcmBin(questionFieldsList,answer,True,timestampStr)]
        for proposal in question["proposals"]:
           questionQcmInfo["propositions"]+=[propositionToQcmBin(questionFieldsList,proposal,False,timestampStr)]

        qcmbinData["qcms"]+=[questionQcmInfo]
    
    saveDictToJson(qcmbinData,fileName)

def quizToQcmSum(quizData,fileName, timestampStr="2022-06-20T07:56:07.869+0200"):
    nbQuestions=len(quizData["questions"])
    duration_per_img_msec=quizData["config"]["duration_per_img_msec"]
    totalDuration_msec=int(duration_per_img_msec)*nbQuestions
    qcmsumData={
        "authorIdContributionMap": {
            QCM_AUTHOR_ID: nbQuestions
        },
        "config": {
            "autoCorrectionEnable": True,
            "duration": totalDuration_msec,
            "buildToolsVersion":6.1,
            "codeVersion":1,
            "language": quizData["config"]["language"],
            "marksPolicyDefinition": "one_per_success",
            "maxRandom": -1,
            "protectionDescription": {},
            "randomEnable": True,
            "showCorrectionEnable": True,
            "showCorrectionMode": 0,
            "smartChoiceEnable": True,
            "totalQuestionCount": nbQuestions,
            "userPermission": -1
        },
        "contributors": [
            {
                "id": "0",
                "bibliography": "Generated with MetaindeX tool.",
                "exposed": True,
                "email": quizData["config"]["author_email"],
                "firstName": quizData["config"]["author_name"],
                "name": quizData["config"]["author_name"],
                "photoUri": quizData["config"]["author_pic"],
                "webSite": quizData["config"]["author_website"],
                "gender": "Autres",
                "phone": "",
                "type": "",
                "createdAt": timestampStr,
                "updatedAt": timestampStr                
            }
        ],
        "createdAt": timestampStr,
        "description": quizData["config"]["quiz_description"],
        "extras": {
            "supports": "all",
            "base_author_id": "1",
            "app_origin": "MetaindeX - csv2quiz.py"
        },
        "id": "2",    
        "keyWords": [],
        "minLevel": {
            "createdAt": timestampStr,
            "description": "",
            "difficulty": 1,
            "id": "en-21",
            "range": 0,
            "title": "",
            "updatedAt": timestampStr
        },
        "recommendedLevel": {
            "createdAt": timestampStr,
            "description": "",
            "difficulty": 1,
            "id": "en-21",
            "range": 0,
            "title": "",
            "updatedAt": timestampStr
        },
        "referenceFeatureMap": {},
        "subject": {
            "createdAt": timestampStr,
            "description": quizData["config"]["subject_description"],
            "iconUri": quizData["config"]["subject_pic"],
            "id": "4",
            "title": quizData["config"]["subject_title"],
            "updatedAt": timestampStr
        },
        "title": quizData["config"]["quiz_title"],
        "updatedAt": timestampStr,
        "uriMap": {
            "images": quizData["config"]["quiz_pic"]
        }
    }

    saveDictToJson(qcmsumData,fileName)
    

def savequizToQcm(quizData,qcmFolderName,resourcesFolder=None,daemonOption=False):

    os.mkdir(qcmFolderName)
    
    # create data file
    quizToQcmBin(quizData,qcmFolderName+os.sep+QCM_DATAFILE_NAME)
    
    # copy resources folder if any
    imgPrefix=IMGPREFIX
    if resourcesFolder!=None:
        targetPath=qcmFolderName+os.sep+imgPrefix
        Path(targetPath).mkdir(parents=True,exist_ok=False)
        
        imagesList=[]

        for questionData in quizData["questions"]:
            for img in questionData["picturesList"]:                
                imagesList+=[img]
        printProgressBar(0, len(imagesList),prefix='Copying '+str(len(imagesList))+' Images:',short=daemonOption)
        nbImg=0
        notFoundImgList=[]
        quizPicIdx=None
        if len(imagesList)>0:
            quizPicIdx=random.randint(0,len(imagesList)-1)
        for img in imagesList:
            nbImg=nbImg+1
            # choose a random image as a quiz thumbnail
            # if not explicitly defined by user
            if nbImg==quizPicIdx:
                quizData["config"]["quiz_pic"]=img
            # img file name already include imgprefix in its name
            fileBaseName=img.replace(imgPrefix,"")
            srcFile=resourcesFolder+os.sep+fileBaseName
            targetFile=targetPath+os.sep+fileBaseName
            #print("cp "+srcFile+" -> "+targetFile)
            if not os.path.isfile(srcFile):
                notFoundImgList+=[srcFile]
            else:
                shutil.copy(srcFile,targetFile)
            printProgressBar(nbImg, len(imagesList),prefix='Copying '+str(len(imagesList))+' Images:',short=daemonOption)        
        if len(notFoundImgList)>0:
            missingFilesLog="missing_img.txt"
            print("WARNING: "+str(len(notFoundImgList))+" images were listed in data but could not be found in given resources folder '"\
                +resourcesFolder+"'. List available in '"+missingFilesLog+"'")            
            with open(missingFilesLog,"w") as fp:
                for missingFile in notFoundImgList:
                    fp.write(missingFile+"\n")
                
            print("Files list stored in file "+missingFilesLog)

    
    # create summary file
    quizToQcmSum(quizData,qcmFolderName+os.sep+QCM_SUMMARYFILE_NAME)

    # zip folder contents
    print("[compressing archive file ... ]",flush=True)
    shutil.make_archive(qcmFolderName, "zip", qcmFolderName, "./")
    shutil.rmtree(qcmFolderName)
    os.rename(qcmFolderName+".zip",qcmFolderName)
    print("[generated QuizMaker file '"+qcmFolderName+"']",flush=True)


def buildFieldsInfo(csvLines,jsonConf,sep=";",daemonOption=False):
    fieldsInfo={}

    csvHeader=csvLines[0]
    csvHeader.split(sep)

    # will contain number of different values of each field
    fieldsValuesMap={}

    # get position of each field in the CSV file
    print("[computing fields position in CSV]",flush=True)
    pos=0
    for headerFieldName in csvHeader.split(sep):
        headerFieldName=headerFieldName.replace("#","")
        fieldsInfo[headerFieldName]={
            "pos":pos,
            "nbValues":0
        }
        fieldsValuesMap[headerFieldName]=[]
        pos=pos+1

    lineNb=0
    iterNb=0
    printProgressBar (0, len(csvLines), prefix="Analyzing Fields:",short=daemonOption)
    for line in csvLines:
        lineNb=lineNb+1
        if lineNb%500==0:
            iterNb=iterNb+1
            printProgressBar(lineNb, len(csvLines), prefix="Analyzing Fields:",short=daemonOption)
        if lineNb==1:
            continue
        if line[0]=="#":
            continue
        lineValues=line.split(sep)
        for fieldName in fieldsInfo.keys():
            fieldPos=fieldsInfo[fieldName]["pos"]
            curFieldValue=lineValues[fieldPos].lower()
            if len(curFieldValue)>0 and curFieldValue not in fieldsValuesMap[fieldName]:
                #print("Field '"+fieldName+"' Significant Value:"+str(curFieldValue))
                fieldsValuesMap[fieldName]+=[curFieldValue]
    printProgressBar(len(csvLines), len(csvLines), prefix="Analyzing Fields:",short=daemonOption)
    for fieldName in fieldsInfo.keys():
        fieldsInfo[fieldName]["nbValues"]=len(fieldsValuesMap[fieldName])

    return fieldsInfo

# ensure that given line does not match any of given values for each of given fields to match
def areAllFieldsEmpty(lineTbl,fieldsToMatchSetList,fieldsInfo):

    if not isinstance(fieldsToMatchSetList,list):
        fieldsToMatchSetList=[fieldsToMatchSetList]

    if len(fieldsToMatchSetList)==0:
        return True

    allFieldsEmpty=True
    for fieldsToMatchMap in fieldsToMatchSetList:
        for fieldStr in fieldsToMatchMap.keys():
            fieldPos=fieldsInfo[fieldStr]["pos"]
            fieldValue=lineTbl[fieldPos]
            if len(fieldValue)>0:
                #print("###      "+fieldStr+":'"+fieldValue+"'")
                allFieldsEmpty=False
        if allFieldsEmpty==False:
            return False

    return True

# ensure that given line does not match any of given values for each of given fields to match
def areFieldsNotMatchingForAll(lineTbl,fieldsToMatchSetList,fieldsInfo):

    if not isinstance(fieldsToMatchSetList,list):
        fieldsToMatchSetList=[fieldsToMatchSetList]

    if len(fieldsToMatchSetList)==0:
        return True

    for fieldsToMatchMap in fieldsToMatchSetList:     
        matchingCurrent=True   
        for fieldStr in fieldsToMatchMap.keys():
            fieldPos=fieldsInfo[fieldStr]["pos"]
            fieldValue=lineTbl[fieldPos]
            if fieldValue.lower()!=fieldsToMatchMap[fieldStr].lower():
                matchingCurrent=False
                break
        if matchingCurrent==True:
            return False
    return True

def getFieldsValuesMap(lineTbl,fieldsToGetList,fieldsInfo,confJson):
    result={}
    picFile=None
    for fieldStr in fieldsToGetList:
        fieldPos=fieldsInfo[fieldStr]["pos"]
        fieldValue=lineTbl[fieldPos]
        if isAPicture(fieldValue):
            fieldValue=IMGPREFIX+fieldValue
            picFile=fieldValue
        result[fieldStr]=fieldValue
    return result,picFile

def fieldsListsEquals(list1,list2):
    for fieldName in list1:
        if fieldName not in list2:
            return False
    for fieldName in list2:
        if fieldName not in list1:
            return False
    return True

def extractUnusedFields(usedFieldsList,availableFieldsList):
    result=[]

    def isInList(fieldName,fieldsList):
        if isinstance(fieldsList,list):
            if fieldName in fieldsList:
                return True
        elif availElem==fieldsList:
            return True
        return False

    for availElem in availableFieldsList:        
        if isinstance(availElem,list):
            for fieldName in availElem:
                if not isInList(fieldName,usedFieldsList):                    
                    result+=[fieldName]
        else:
            if not isInList(availElem,usedFieldsList):                    
                result+=[availElem]
        
    return result

def generateQuestion(csvLines,confJson,fieldsInfo,nbAnswers,sep=";"):
    _debug=False
    questionData={}
    nbLines=len(csvLines)
    lineNb=random.randint(1,nbLines-1)
    questionData["lineNb"]=lineNb        
    lineValues=csvLines[lineNb].split(sep)
    nbTries=0
    if _debug:
        print("\tline "+str(lineNb))
        #print("\tline decoded")

# prepare data for pres, question and answers
    # chose fields to be shown as item presentation
    # if all fields empty for this line, try another line
    questionDataOkForThisLine=False
    presFieldNames=None
    while questionDataOkForThisLine==False:
        nbTries=nbTries+1
        
        questionData["presFields"]={}    
        questionData["picturesList"]=[]
        questionData["unusedFieldsValues"]={}
        lineNb=random.randint(1,nbLines-1)
        questionData["lineNb"]=lineNb    
        lineValues=csvLines[lineNb].split(sep)

        presFieldsEmpty=True
        presFieldEnumIdx=random.randint(0,len(confJson["pres_fields"])-1)
        presFieldNames=confJson["pres_fields"][presFieldEnumIdx]    
        for fieldName in presFieldNames:
            fieldEnums=fieldsInfo[fieldName]
            fieldCsvPos=fieldEnums["pos"]
            fieldLineValue=lineValues[fieldCsvPos]
            if isAPicture(fieldLineValue):            
                fieldLineValue=IMGPREFIX+fieldLineValue
                questionData["picturesList"]+=[fieldLineValue]
            if len(fieldLineValue)>0:
                presFieldsEmpty=False
            questionData["presFields"][fieldName]=fieldLineValue
        # try another line if chosen pres fields empty for
        if presFieldsEmpty==True:
            if nbTries>=MAX_NB_TRIES_FOR_QUESTION:
                presfieldsList=""
                for fieldName in presFieldNames:
                    presfieldsList=presfieldsList+" "+fieldName
                print("ERROR: unable to find a non empty presentation:"+ presfieldsList)
                sys.exit(1)
            continue

        # update list of unused fields, used later to build comment of the question
        for unusedField in extractUnusedFields(presFieldNames,confJson["pres_fields"]):
            fieldEnums=fieldsInfo[unusedField]
            fieldCsvPos=fieldEnums["pos"]
            fieldLineValue=lineValues[fieldCsvPos]
            if not isAPicture(fieldLineValue) and len(fieldLineValue)>0:
                questionData["unusedFieldsValues"][unusedField]=fieldLineValue
        if _debug:
            print("\tPres:")
            print(questionData["presFields"])
        # chose fields to be asked as a question    
        questionFieldEnumIdx=presFieldEnumIdx
        
        #print("\tpreparing question fields names ...")
        questionFieldEnumIdx=random.randint(0,len(confJson["question_fields"])-1)
        questionFieldNames=confJson["question_fields"][questionFieldEnumIdx] 
        while fieldsListsEquals(questionFieldNames,presFieldNames):
            questionFieldEnumIdx=random.randint(0,len(confJson["question_fields"])-1)
            questionFieldNames=confJson["question_fields"][questionFieldEnumIdx] 
        
        if _debug:
            print("\tQuestion:")
            print(questionFieldNames)
            print("\tpreparing answer ...")
        chosenProposals={}
        questionData["answer"]={}
        answerEmpty=True
        for fieldName in questionFieldNames:
            curQuestionFieldDef=fieldsInfo[fieldName]
            curQuestionFieldPos=curQuestionFieldDef["pos"]       
            answerValue=lineValues[curQuestionFieldPos]
            if isAPicture(answerValue):
                answerValue=IMGPREFIX+answerValue
                questionData["picturesList"]+=[answerValue]
            if len(answerValue)>0:
                answerEmpty=False
            questionData["answer"][fieldName]=answerValue
            chosenProposals[fieldName]=[]
            # if field is used for answer, remove it from "unused fields" list
            if fieldName in questionData["unusedFieldsValues"]:
                questionData["unusedFieldsValues"][fieldName]=None
        # try another line if chosen answer empty
        if answerEmpty==True:            
            if nbTries>=MAX_NB_TRIES_FOR_QUESTION:
                questionfieldsList=""
                for fieldName in questionFieldNames:
                    questionfieldsList=questionfieldsList+" "+fieldName
                print("ERROR: unable to find a non empty answer:"+ questionfieldsList)
                sys.exit(1)
            continue
        
        # update list of unused fields, used later to build comment of the question
        for unusedField in extractUnusedFields(questionFieldNames,confJson["question_fields"]):
            fieldEnums=fieldsInfo[unusedField]
            fieldCsvPos=fieldEnums["pos"]
            fieldLineValue=lineValues[fieldCsvPos]
            if not isAPicture(fieldLineValue) and len(fieldLineValue)>0:
                questionData["unusedFieldsValues"][unusedField]=fieldLineValue
        
        if _debug:
            print("\tAnswer:")
            print(questionData["answer"])

        questionData["proposals"]=[]
            
        # if a given field has only 2 values, we cannot look for 4 different values and shall
        # stop at 2 possible answers.
        fieldsMaxNbValues=0
        for questionField in questionFieldNames:
            #print(["Field '"+questionField+"': "+str(fieldsInfo[questionField]["nbValues"])+" nbValues in CSV"])
            fieldsMaxNbValues=max(fieldsMaxNbValues,fieldsInfo[questionField]["nbValues"])
        nbAnswers=min(nbAnswers,fieldsMaxNbValues)

        #print("\tpreparing proposals ...")
        linesToIgnore=[1, lineNb]
        nbTries=0
        for i in range(nbAnswers-1):
            noMoreAnswers=False
            isProposalOk=False
            testedLines=linesToIgnore
            nbTries=0
            while not isProposalOk:
                nbTries=nbTries+1
                proposalLineNb=random.randint(1,nbLines-1)
                if _debug:
                    print("testing l."+str(proposalLineNb)+" as answer\n")
                
                # avoid to spend hours looking for THE one different value in the quizz
                testedAllLines=len(testedLines)==len(csvLines)-1
                reachedMaxNbTries=nbTries==MAX_NB_TRIES_FOR_PROPOSAL
                if testedAllLines or reachedMaxNbTries:
                    if _debug:
                        print("\t\t--> sorry no additional interresting answer in given data (reachedMaxNbTries="+str(reachedMaxNbTries)+" testedAllLines="+str(testedAllLines)+")")
                        #print(testedLines)
                    noMoreAnswers=True
                    break

                if proposalLineNb in testedLines:
                    if _debug:
                        print("\t\tline already tested")
                    continue

                testedLines+=[proposalLineNb]              
                proposalLine=csvLines[proposalLineNb]
                proposalLineTbl=proposalLine.split(sep)                        
                isEmptyProposal=areAllFieldsEmpty(proposalLineTbl,questionData["answer"],fieldsInfo)
                notMatchingPres=areFieldsNotMatchingForAll(proposalLineTbl,questionData["presFields"],fieldsInfo)
                notMatchingGoodAnswer=areFieldsNotMatchingForAll(proposalLineTbl,questionData["answer"],fieldsInfo)
                notMatchingOtherProposals=areFieldsNotMatchingForAll(proposalLineTbl,questionData["proposals"],fieldsInfo)
                if _debug:
                    print("\t\tFor l."+str(lineNb)+" : l."+str(proposalLineNb)+": notMatchingPres="+str(notMatchingPres)+" notMatchingGoodAnswer="+str(notMatchingGoodAnswer)+" notMatchingOtherProposals="+str(notMatchingOtherProposals))
                
                isProposalOk=not isEmptyProposal and notMatchingPres and notMatchingGoodAnswer and notMatchingOtherProposals                
                if isProposalOk:
                    linesToIgnore+=[proposalLineNb]
                    if _debug:
                        print("\t\tl."+str(proposalLineNb)+" as proposal")
                    curProposal,picFile=getFieldsValuesMap(proposalLineTbl,questionFieldNames,fieldsInfo,confJson)
                    if picFile != None:
                        questionData["picturesList"]+=[picFile]
                    questionData["proposals"]+=[curProposal]                        

            if noMoreAnswers==True:
                break
        
        # maybe add here complementary checks to ensure question meets all quality expectations
        questionDataOkForThisLine=True
    #print(questionData)                 
    return questionData

def proposal2screen(fieldsList,proposalData,position):
    text=str(position)+":"
    for fieldStr in fieldsList:
        value=proposalData[fieldStr]
        text+=" "+value
    return "\t"+text

def question2screen(questionData):

    presData=questionData["presFields"]
    presStr="\n\t### quiz ###\n\n"
    for presFieldName in presData.keys():
        fieldValue=presData[presFieldName]
        presStr+="\t"+presFieldName+": "+fieldValue+"\n"
    
    print(presStr)

    answer=questionData["answer"]
    questionFieldsList=answer.keys()    
    questionStr="Question: "
    for questionFieldName in questionFieldsList:
        questionStr+=" "+questionFieldName
    print("\t"+questionStr+" ?")

    nbProposals=len(questionData["proposals"])
    goodAnswerPos=random.randint(1,nbProposals)
    curPos=1
    for proposal in questionData["proposals"]:
        if curPos==goodAnswerPos:
            print(proposal2screen(questionFieldsList,questionData["answer"],curPos)+" *")
            curPos+=1
        print(proposal2screen(questionFieldsList,proposal,curPos))
        curPos+=1
    
    print("\nComment:")
    print(questionData["unusedFieldsValues"])
    #print(questionData)

if __name__ == "__main__":

    # Define and parse arguments.
    parser = argparse.ArgumentParser(prog="[MetaindeX.fr Toolbox] "+__file__,description=DESC_TXT,formatter_class=RawFormatter)
    parser.add_argument("nbq",help="Nb questions to generate")
    parser.add_argument("--input",help="if defined, take given json quiz file rather than CSV+config.")
    parser.add_argument("--output", default="quiz.json",help="file to generate quiz to. Default is to 'quiz.json'. Allowed extensions: .json|.qcm. QCM format is to be used with Android App 'QuizMaker'.")
    parser.add_argument("--imgpath",help="folder containing resources (images etc) to be stored into resources ('res/') folder of the quiz")
    parser.add_argument("-v", action="store_true",help="show on console a textual version of each generated question")
    parser.add_argument("-f", action="store_true",help="overwrite existing output if exist")
    parser.add_argument("-d", action="store_true",help="daemon mode: show progress messages optimized for being called from third party app.")
    parser.add_argument("configfile",nargs="?",help="JSON file containing configuration data")
    parser.add_argument("csvfile",nargs="?",help="CSV file containing data to base the quiz on")
    
    args = parser.parse_args()

    quizData=None
    if args.input!=None:
        quizData=loadJson(args.input)
    else:
        print("[loading Config]",flush=True)
        if args.configfile==None:
            print("ERROR: missing input argument: configfile")
            sys.exit(1)

        if not os.path.isfile(args.configfile):
            print("ERROR: given config file not reachable : '"+args.configfile+"'")
            sys.exit(1)
        try:
            confJson=loadJson(args.configfile)
        except Exception as e:
            print("ERROR: "+str(e))

        print("[loading CSV]",flush=True)
        csvLines=open(args.csvfile).read().splitlines()

        print("[creating contents map]",flush=True)
        fieldsInfo=buildFieldsInfo(csvLines,confJson, daemonOption=args.d)
        #print(fieldsInfo)

        printProgressBar (0, int(args.nbq), prefix="Generating "+args.nbq+" Questions:",short=args.d)
        quizData={ "fiedsInfo":fieldsInfo, "config":confJson["config"], "questions":[] }
        for i in range(int(args.nbq)):            
            nbAnswers=confJson["config"]["nb_answers"]
            questionData=generateQuestion(csvLines,confJson,fieldsInfo,int(nbAnswers))
            quizData["questions"]+=[questionData]
            printProgressBar (i, int(args.nbq), prefix="Generating "+args.nbq+" Questions:",short=args.d)
            if (args.v==True):
                question2screen(questionData)
        printProgressBar (int(args.nbq), int(args.nbq), prefix="Generating "+args.nbq+" Questions:",short=args.d)

    if os.path.isfile(args.output):
        if args.f==True:
            #os.remove
            if os.path.isdir(args.output):
                shutil.rmtree(args.output)
            else:
                os.remove(args.output)
                
        else:
            print("ERROR: output file exists, pease remove it or use '-f' option.")
            os.exit(1)
    if args.output.endswith(".json"):
        saveDictToJson(quizData,args.output)
    else:
        savequizToQcm(quizData,args.output,args.imgpath, daemonOption=args.d)
    
    sys.exit(0)
    
