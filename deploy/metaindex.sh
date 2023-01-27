#!/bin/bash


script_name=$(basename $0)
script_dir=$(dirname $0)

source $script_dir/metaindex.common.sh

RUNMODE=server
DEBUG=
DOCKER_ENV_ROOT=".env"
STATUS_ONLY=0

function patchFileDebug() {
    
    file=$1
    if [ ! -f "$file" ]; then
        >&2 echo "ERROR: unable to patch file for debug, file not reachable: $file"
        exit 1        
    fi

    setDebug=$2
    if [ "$setDebug" == "1" ]; then
        echo ["activating DEBUG in $file"]
        perl -pi -e 's/^(\s*)#([^#]+) ### DEBUG/$1$2 ### DEBUG/g' $file
    else
        echo ["deactivating DEBUG in $file"]
        perl -pi -e 's/^(\s*)([^#]+) ### DEBUG/$1#$2 ### DEBUG/g' $file
    fi

    if [ "$?" != "0" ]; then
        >&2 echo "ERROR: unable to patch file for debug: $file"
        exit 1        
    fi
}

function getCurrentRunMode() {
    grep RUNMODE $DOCKER_ENV_ROOT.current
}
function getCurrentDebugMode() {
    egrep -e '^\s*#[^#]+### DEBUG\s*$' docker-compose.yml >/dev/null
    echo "DEBUG=$?"
}

function showStatus() {
    runMode=$(getCurrentRunMode)
    debug=$(getCurrentDebugMode)
    echo "$runMode // $debug"
    showRunningStatus
}
function usage() {
	echo " $script_name [OPTIONS] START|STOP|RESTART"
    echo
	echo "      start/stop metaindex app."
    echo
    echo "      OPTIONS:"
    echo "        -h : show help"
    echo "        -d <0|1>: if 1 start in debug mode (mxwebapp not launched and needed ports open for run in debug mode)"
    echo "        -m <mode> : start metaindex in given mode: server|standalone (default is server)"
    echo "        -s: simply give status of current conf"    
    echo
}

while getopts "hd:m:f:se" option; do
		case ${option} in
			h) usage;exit 1;;
            d) DEBUG=$OPTARG;; 
            m) RUNMODE=$OPTARG;;            
            s) showStatus; exit 0;;
		esac
done
shift $((OPTIND -1))

CMD=$1
if [ -z "$CMD" ]; then
    >&2 echo "ERROR: missing command: START|STOP|RESTART"
    echo
    usage
    exit 1
fi

if [ "$RUNMODE" != "server" ] && [ "$RUNMODE" != "standalone" ]; then
    >&2 echo "ERROR: unrecognized runmode: allowed values are server|standalone, given was '$RUNMODE'"
    echo
    usage
    exit 1
fi

if [ "$CMD" == "STOP" ] || [ "$CMD" == "RESTART" ]; then
    docker-compose --env-file $DOCKER_ENV_ROOT.current down    
fi

if [ "$CMD" == "STOP" ]; then
    exit 0;
fi

if [ -z "$RUNMODE" ]; then
    if [ -f "$DOCKER_ENV_ROOT.current" ]; then
        RUNMODE=$(getCurrentRunMode | sed 's/RUNMODE=//')
        echo "[reuse previous RUNMODE=$RUNMODE]"
    else 
        RUNMODE="server"
        echo "[using default RUNMODE=$RUNMODE]"
    fi
fi

if [ -z "$DEBUG" ]; then
    if [ -f "$DOCKER_ENV_ROOT.current" ]; then
        DEBUG=$(getCurrentDebugMode | sed 's/DEBUG=//')
        echo "[reuse previous DEBUG=$DEBUG]"
    else 
        DEBUG=0
        echo "[using default DEBUG=$DEBUG]"
    fi
fi
rm -f $DOCKER_ENV_ROOT.current
cp $DOCKER_ENV_ROOT.$RUNMODE $DOCKER_ENV_ROOT.current

if [ "$DEBUG" == "1" ]; then
    cat  $DOCKER_ENV_ROOT.debug >> $DOCKER_ENV_ROOT.current
elif [ "$RUNMODE" == "server" ]; then
    echo "[Extracting config for for PROD SERVER env]"
    ./tools/server_install/decode_secrets.sh >> $DOCKER_ENV_ROOT.current
    if [ "$?" != "0" ]; then
        >&2 echo "ERROR: unable to extract prod config params, sorry"
        exit 1
    fi
fi

# activate/inhibate DEBUG ports so that
# debug of webapp in eclipse can connect
patchFileDebug docker-compose.yml $DEBUG

option=""
if [ "$DEBUG" == "1" ]; then option="noWebApp"; fi
start_mx_server $option

HTTPS_PORT=$(grep 'HTTPS_PORT=' $DOCKER_ENV_ROOT.current | cut -d= -f 2 )
if [ "$HTTPS_PORT" != "443" ]; then
    HTTPS_PORT=":$HTTPS_PORT"
else
    HTTPS_PORT=""
fi

if [  "$DEBUG" == "1" ]; then
    MX_PORT_HTTPS=$(grep MX_PORT_HTTPS $DOCKER_ENV_ROOT.current | sed 's/MX_PORT_HTTPS=//')
    echo "You can now start your own instance of webapp and connect through port $MX_PORT_HTTPS"
elif [  "$RUNMODE" == "standalone" ]; then
    echo
    echo "You can now start firefox at https://localhost$HTTPS_PORT"
    echo "You can accept security exception regarding self-signed certificate since you're hosting yourself the app."
    echo
elif [  "$RUNMODE" == "server" ]; then
    echo
    echo "You can now start firefox at https://metaindex.fr$HTTPS_PORT"
    echo "You can accept security exception regarding self-signed certificate since you're hosting yourself the app."
    echo
fi



exit 0