#!/bin/bash


script_name=$(basename $0)
script_dir=$(dirname $0)

source $script_dir/metaindex.common.sh

MODULE_NAME=
function usage() {
	echo " $script_name [OPTIONS] [module name]"
    echo
	echo "      Stop metaindex app (standalone distribution)."
    echo
    echo "      OPTIONS:"
    echo "        -h : show help" 
    echo
}

while getopts "h" option; do
		case ${option} in
			h) usage;exit 1;;
		esac
done
shift $((OPTIND -1))

MODULE_NAME=$1

if [ ! -f .env.current ]; then
    docker stop $MODULES_LIST
    exit $?
elif [ ! -z "$MODULE_NAME" ]; then
    docker stop $MODULE_NAME
else
    docker-compose --env-file .env.current down   
fi
exit $?
