#!/bin/bash
# this script is called from docker-compose file to create metaindex user in ELK

script_name=$(basename $0)
script_dir=$(dirname $0)
source $script_dir/../mx_tools_commons.sh
source $script_dir/mxelsrc_commons.sh

MX_USER=metaindex
MX_EMAIL="admin@metaindex.fr"
MX_ROLES='"superuser","kibana_admin"'

function usage() {
	echo "$script_name metaindex_password kibana_password elasticpwd"
	echo "	initialise elasticsearch users and conf for MetaindeX app."
	echo
}

while getopts "h" option; do
		case ${option} in
			h) usage;exit 1;; # $OPTARG
		esac
done
shift $((OPTIND -1))

METAINDEX_PASSWD=$1
KIBANA_PASSWD=$2
ELASTIC_PASSWD=$3

ELK_SERVER=localhost
ELK_PORT=9200

if [ -z "$METAINDEX_PASSWD" ] || [ -z "$KIBANA_PASSWD" ] || [ -z "$ELASTIC_PASSWD" ]; then
    >&2 echo -e $RED"parameters missing"$NORMAL
    usage
    exit 1
fi

$script_dir/mxelsrc_status.sh -w green -p $ELASTIC_PASSWD

# set kibana password
$script_dir/mxelsrc_user_setpwd.sh -p $ELASTIC_PASSWD kibana $KIBANA_PASSWD 
# create metaindex user
$script_dir/mxelsrc_user_create.sh -p $ELASTIC_PASSWD metaindex $METAINDEX_PASSWD $MX_EMAIL "$MX_ROLES" 