#!/bin/bash


# colors for echo -e
NORMAL="\\033[0;39m"
RED="\\033[0;31m"
ERROR="\\033[1;31m"
GREEN="\\033[0;32m"
SUCCESS="\\033[0;32m"
BLUE="\\033[0;34m"
PURPLE="\\033[0;35m"
YELLOW="\\033[0;33m"
LIGHTGRAY="\\033[0;37m"
LIGHTBLUE="\\033[0;36m"
GRAY="\\033[0;2m"


# dev server
DEV_FOLDER=$HOME/dev/workspace
DEV_TARBALLS_PATH=$DEV_FOLDER/mxdeliveries
DEV_DOCKER_PATH=$DEV_FOLDER/metaindex
DEV_TARGET_NAME=deploy

TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
TMP_FOLDER=$HOME/tmp/mx
TMP_FILE=$TMP_FOLDER/metaindex.tmp
mkdir -p $TMP_FOLDER


# on prod server
MX_SERVER=metaindex.fr
MX_SERVER_HOME_DEV=$HOME/dev/workspace/archives
MX_SERVER_HOME_PROD=/home/$MX_SERVER_USER
# MX_SERVER_USER to be defined by calling script
if [ -z "$MX_SERVER_HOME" ]; then
	MX_SERVER_HOME=$MX_SERVER_HOME_PROD
	if [ ! -p "$MX_SERVER_HOME" ] && [ "$MX_SERVER_USER" == "root" ]; then
		MX_SERVER_HOME=/root
	fi
	if [ ! -d "$MX_SERVER_HOME" ]; then
		MX_SERVER_HOME=$MX_SERVER_HOME_DEV
	fi
fi
MX_SERVER_WORK_PATH=$MX_SERVER_HOME/mxmaintenance
MX_SERVER_BACKUPS_PATH=$MX_SERVER_WORK_PATH/backups
MX_SERVER_TARBALLS_PATH=$MX_SERVER_WORK_PATH/deliveries
MX_SERVER_TARBALLS_PATH_PROD=$MX_SERVER_HOME_PROD/mxmaintenance/deliveries

MX_LOCAL_HOME=$HOME
MX_LOCAL_WORK_PATH=$MX_LOCAL_HOME/mxmaintenance
MX_LOCAL_BACKUPS_PATH=$MX_LOCAL_WORK_PATH/backups
MX_LOCAL_TARBALLS_PATH=$MX_LOCAL_WORK_PATH/deliveries
mkdir -p $MX_LOCAL_BACKUPS_PATH $MX_LOCAL_TARBALLS_PATH

# ElasticSearch image mxelsrc
ELK_SERVER=localhost
ELK_PORT=9200
KIBANA_SERVER=localhost
KIBANA_PORT=5601
ELK_BACKUP_NAME=mx_backups
if [ -f "docker-compose.yml" ]; then
ELK_INNER_SNAPSHOTS_FOLDER=$(grep '\- mxelsrc-backups' docker-compose.yml | sed 's/.*- mxelsrc-backups://')
fi

# MySql image mxmysql
MYSQL_INNER_BACKUPS_FOLDER=/root/mxmaintenance/backups

# MxWebApp
if [ -f ".env" ]; then
WEBAPP_INNER_USERDATA_FOLDER=$(grep 'MX_USERDATA_PATH=' .env | sed 's/MX_USERDATA_PATH=//')
fi

function check_json_status() {
	json=$1
	expectedContents=$2
	
	#>&2 echo
	#>&2 echo json=$json
	#>&2 echo expectedContents=$expectedContents
	#>&2 echo
	
	result="0"
	echo $json | grep exception >/dev/null	
	if [ "$?" == "0" ]; then
		result="1"
	else
		echo $json | grep status >/dev/null	
		if [ "$?" == "0" ]; then
			echo $json | grep '"status" : 0' > /dev/null
			if [ "$?" != "0" ]; then
				result="1"
			fi
		fi
	fi

	if [ ! -z "$expectedContents" ] && [ "$result" == "0" ]; then
		#>&2 echo ">>>"
		#>&2 echo "$json" 
		#>&2 echo "<<<"
		echo "$json" > $TMP_FILE
		grep "$expectedContents"  $TMP_FILE > /dev/null
		if [ "$?" != "0" ]; then
			result="2"
		fi
		rm $TMP_FILE
	fi

	echo $result

}
