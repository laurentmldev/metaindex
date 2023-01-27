#!/bin/bash


script_name=$(basename $0)
script_dir=$(dirname $0)
source $script_dir/../mx_tools_commons.sh
source $script_dir/mxelsrc_commons.sh

ELK_SERVER=localhost
ELK_PORT=9200
SLEEP_LOOP_SEC=3
ELASTIC_PWD=

function usage() {
	echo "$script_name [OPTIONS]"
	echo "	return current status of elasticsearch server : none|red|yellow|green"
    echo
    echo "OPTIONS:"
    echo "  -h : show help"
    echo "  -e <expected> : return 0 is current status matches expected value, 1 otherwise"
    echo "  -w <expected> : wait until current status matches expected value, retry every $SLEEP_LOOP_SEC seconds"
    echo "	-p <password> : password to be used"
    echo
}

WAIT_UNTIL=
EXPECTED=
while getopts "hw:e:p:" option; do
		case ${option} in
			h) usage;exit 1;;
            w) WAIT_UNTIL=1;EXPECTED=$OPTARG;; 
            e) EXPECTED=$OPTARG;;
            p) ELASTIC_PWD=$OPTARG;;
		esac
done
shift $((OPTIND -1))

if [ -z "$ELASTIC_PWD" ]; then
	echo -n "Elasticsearch password: " 
	read -s ELASTIC_PWD
fi

function getCurrentStatus() {
    
    statusLine=$(curl -s -u elastic:$ELASTIC_PWD http://$ELK_SERVER:$ELK_PORT/_cat/health 2>/dev/null)
    if [ "$?" != "0" ] ; then
        echo "none"
        exit 1
    fi

    status=$(echo $statusLine | grep cluster | sed -r 's%.*cluster ([^ 01]+) [0-9].*%\1%')
    if [ "$?" != "0" ] ; then
        echo "none"
        exit 1
    else
        echo $status
    fi

    exit 0
}

if [ -z "$WAIT_UNTIL" ]; then
    status=$(getCurrentStatus)
    if [ ! -z "$EXPECTED" ]; then
        if [ "$EXPECTED" == "$status" ]; then
            exit 0
        else   
            exit 1
        fi
    else
        echo $status
        exit 0
    fi
fi

#timeout $WAIT_TIMEOUT bash -c "until $WAIT_COMMAND; do sleep $WAIT_SLEEP; done"
elkReady=0
while [ "$elkReady" == "0" ]; do    
    status=$(getCurrentStatus)
    if [ "$EXPECTED" == "$status" ]; then
        elkReady=1
    else
        echo "waiting for ELK '$EXPECTED' flag (current is '$status') ... "
        sleep $SLEEP_LOOP_SEC       
    fi
done

exit 0