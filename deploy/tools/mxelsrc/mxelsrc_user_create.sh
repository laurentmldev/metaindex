#!/bin/bash


script_name=$(basename $0)
script_dir=$(dirname $0)
source $script_dir/../mx_tools_commons.sh
source $script_dir/mxelsrc_commons.sh

ELASTIC_PWD=

function usage() {
	echo "$script_name user passwd email roles"
	echo "	create given user in running ELK keystore"
    echo
    echo "  OPTIONS:"
    echo "		-p <password>: elastic-password to be used"
}

while getopts "hp:" option; do
		case ${option} in
			h) usage;exit 1;; # $OPTARG
            p) ELASTIC_PWD=$OPTARG;;
		esac
done
shift $((OPTIND -1))

ELK_USER=$1
ELASTIC_USER_PWD=$2
ELK_EMAIL=$3
ELK_ROLES=$4

if [ -z "$ELASTIC_PWD" ]; then
	echo -n "Elasticsearch password: " 
	read -s ELASTIC_PWD
fi

ELK_SERVER=localhost
ELK_PORT=9200

if [ -z "$ELK_USER" ] || [ -z "$ELASTIC_USER_PWD" ] || [ -z "$ELK_EMAIL" ] || [ -z "$ELK_ROLES" ] || [ -z "$ELASTIC_PWD" ]; then
    >&2 echo -e $RED"parameters missing"$NORMAL
    usage
    exit 1
fi

jsonDef="{\"password\":\"$ELASTIC_USER_PWD\",\"email\":\"$ELK_EMAIL\",\"roles\":[$ELK_ROLES]}"
curl -s -u elastic:$ELASTIC_PWD -XPOST http://$ELK_SERVER:$ELK_PORT/_security/user/$ELK_USER  --header "content-type: application/JSON"  --data $jsonDef
if [ "$?" != "0" ]; then
    >@2 echo $RED"Enable to create user $ELK_USER"$NORMAL
    exit 1
fi

exit 0