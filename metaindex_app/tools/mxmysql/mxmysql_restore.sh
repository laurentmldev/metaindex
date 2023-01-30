#!/bin/bash

script_name=$(basename $0)
script_dir=$(dirname $0)

ELK_DB_NAME="metaindex"
MX_SERVER_USER=root
source $script_dir/../mx_tools_commons.sh

FORCE=0
MYSQL_PWD=
SILENT=0
function usage() {
	echo "$script_name snapshot_id"
	echo "	restore contents of MX mysql database corresponding to given ID "
	echo
	echo "	OPTIONS:"
	echo "		-s : silent"
	echo "		-f : force : don't ask for confirmation"
	echo "		-p : password to be used"
}

while getopts "hfp:s" option; do
		case ${option} in
			f) FORCE=1;;
			s) SILENT=1;;
			h) usage;exit 1;; # $OPTARG
			p) MYSQL_PWD=$OPTARG;;
		esac
done
shift $((OPTIND -1))

SNAPSHOT_NAME=$1
if [ -z "$SNAPSHOT_NAME" ]; then
	>&2 echo -e $RED"ERROR: missing input parameter 'snapshot_id'"$NORMAL
	ls -l $MX_SERVER_BACKUPS_PATH
	exit 1
fi

sqlFile=$MX_SERVER_BACKUPS_PATH/$SNAPSHOT_NAME.sql
if [ ! -f "$sqlFile" ]; then
	>&2 echo -e $RED"ERROR: corresponding SQL backup not reachable : $sqlFile"$NORMAL
	ls -l $MX_SERVER_BACKUPS_PATH
	exit 1
fi


if [ "$FORCE" == "0" ]; then
	ls -l $sqlFile	
	echo -ne $YELLOW"replace current MySQL database with contents of this snapshot ? (y/n) "$NORMAL
	read response
	if [ "$response" != "y" ]; then
		echo "restore processing aborted by user"
		exit 1
	fi
fi

if [ "$SILENT" != "1" ]; then
	echo -n "restoring MYSQL contents from $sqlFile ..."
fi
#mysqlimport $ELK_DB_NAME $sqlFile
mysql -u mxsql -p$MYSQL_PWD metaindex < $sqlFile
if [ "$?" != "0" ]; then
	>&2 echo -e $ERROR"	FAILED"$NORMAL
	exit 1
else
	if [ "$SILENT" != "1" ]; then
		echo -e $SUCCESS"	done"$NORMAL
	fi
fi

exit 0
