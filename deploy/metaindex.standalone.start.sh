#!/bin/bash


script_name=$(basename $0)
script_dir=$(dirname $0)

source $script_dir/metaindex.common.sh

RUNMODE=standalone
STATUS_ONLY=0

function usage() {
	echo " $script_name [OPTIONS] "
    echo
	echo "      Start metaindex app (standalone distribution)."
    echo
    echo "      OPTIONS:"
    echo "        -h : show help"
    echo "        -s: simply give status of current conf"    
    echo "        -w: webapp only"
    echo "        -b: backapps only"
    echo
}

startOption=""
while getopts "hsbw" option; do
		case ${option} in
			h) usage;exit 1;;
            s) showRunningStatus; exit 0;;
            b) startOption="noWebApp";;
            w) startOption="onlyWebApp";;            
		esac
done
shift $((OPTIND -1))

rm -f $DOCKER_ENV_ROOT.current
checkWslConfig
cp $DOCKER_ENV_ROOT.$RUNMODE $DOCKER_ENV_ROOT.current
start_mx_server $startOption

HTTPS_PORT=$(grep 'HTTPS_PORT=' $DOCKER_ENV_ROOT.current | cut -d= -f 2)
if [ "$HTTPS_PORT" != "443" ]; then
    HTTPS_PORT=":$HTTPS_PORT"
else
    HTTPS_PORT=""
fi

echo
echo "You can now start firefox at https://localhost$HTTPS_PORT"
echo "You can accept security exception regarding self-signed certificate since you're hosting yourself the app."
echo
exit 0