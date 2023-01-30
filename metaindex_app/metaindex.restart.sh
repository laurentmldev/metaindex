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
    echo
}

while getopts "hs" option; do
		case ${option} in
			h) usage;exit 1;;
            s) showRunningStatus; exit 0;;            
		esac
done
shift $((OPTIND -1))

$script_dir/metaindex.stop.sh mxwebapp
$script_dir/metaindex.start.sh -w

