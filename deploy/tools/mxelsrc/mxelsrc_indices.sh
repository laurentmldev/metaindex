#!/bin/bash


script_name=$(basename $0)
script_dir=$(dirname $0)
source $script_dir/../mx_tools_commons.sh
source $script_dir/mxelsrc_commons.sh

ELK_SERVER=localhost
ELK_PORT=9200
INDICES_PATTERN='*'
ACTION="list"
SILENT="FALSE"
ELASTIC_PWD=

function usage() {
	echo "$script_name [OPTIONS] <elasticpwd> [indices-pattern]"
	echo "	list available indices and associated info"
    echo
    echo "OPTIONS:"
    echo "  -h : show help"
    echo "  -c : close corresponding indices"
    echo "  -o : open corresponding indices"
    echo "  -s : no user message"
    echo "	-p <password> : password to be used"
    echo
}

while getopts "hcosp:" option; do
		case ${option} in
			h) usage;exit 1;;
            c) ACTION="close";;
            o) ACTION="open";;
            s) SILENT="TRUE";;
            p) ELASTIC_PWD=$OPTARG;;
		esac
done
shift $((OPTIND -1))


if [ -z "$ELASTIC_PWD" ]; then
	echo -n "Elasticsearch password: " 
	read -s ELASTIC_PWD
fi

if [ ! -z "$1" ]; then
    INDICES_PATTERN=$1
fi

curl -s -u elastic:$ELASTIC_PWD -XGET http://$ELK_SERVER:$ELK_PORT/_cat/indices/$INDICES_PATTERN?expand_wildcards=all
if [ "$?" != "0" ] ; then
    echo "none"
    exit 1
fi

if [ "$ACTION" == "close" ]; then
    if [ "$SILENT" == "FALSE" ]; then 
        echo -n "closing indices ... "; 
    fi
    stdout=$(curl -s -u elastic:$ELASTIC_PWD -XPOST http://$ELK_SERVER:$ELK_PORT/$INDICES_PATTERN/_close?expand_wildcards=all 2>/dev/null)
    result=$(check_json_status "$stdout")
    if [ "$result" != "0" ] ; then
        if [ "$SILENT" == "FALSE" ]; then 
            echo
            echo -e $ERROR"ERROR ($result): unable to close patterns"$NORMAL
        fi
        echo $stdout
        exit 1
    fi
    if [ "$SILENT" == "FALSE" ]; then 
        echo -e $SUCCESS"done"$NORMAL
    fi
    exit 0
fi

if [ "$ACTION" == "open" ]; then
    if [ "$SILENT" == "FALSE" ]; then 
        echo -n "opening indices ... "
    fi
    stdout=$(curl -s -u elastic:$ELASTIC_PWD -XPOST http://$ELK_SERVER:$ELK_PORT/$INDICES_PATTERN/_open?expand_wildcards=all 2>/dev/null)
    result=$(check_json_status "$stdout")
    if [ "$result" != "0" ] ; then
        if [ "$SILENT" == "FALSE" ]; then 
            echo
            echo -e $ERROR"ERROR ($result): unable to close patterns"$NORMAL
        fi
        echo $stdout
        exit 1
    fi
    if [ "$SILENT" == "FALSE" ]; then 
        echo -e $SUCCESS"done"$NORMAL
    fi
    exit 0
fi
exit 0