#!/bin/bash


script_name=$(basename $0)
script_dir=$(dirname $0)
source $script_dir/../mx_tools_commons.sh

function usage() {
	echo "$script_name"
	echo "	build ready-to-deploy tarball, for target '$DEV_TARGET_NAME'"
}

while getopts "h" option; do
		case ${option} in
			h) usage;exit 1;; # $OPTARG
		esac
done
shift $((OPTIND -1))

function buildServerTarball() {
	tarballName=$1
	targetsFolder=$2
	rm -rf $TMP_FOLDER && mkdir -p $TMP_FOLDER
	cp -RL $targetsFolder/$DEV_TARGET_NAME $TMP_FOLDER/$tarballName
	cd $TMP_FOLDER && tar Lczf $DEV_TARBALLS_PATH/$tarballName.tgz $tarballName && rm -rf $TMP_FOLDER

	echo "generated SERVER $DEV_TARBALLS_PATH/$tarballName.tgz"
	ls -l $DEV_TARBALLS_PATH/$tarballName.tgz
}

function buildStandaloneTarball() {
	tarballName=$1
	targetsFolder=$2

	rm -rf $TMP_FOLDER && mkdir -p $TMP_FOLDER/$tarballName
	cp -RL $targetsFolder/$DEV_TARGET_NAME/docker-compose.yml $TMP_FOLDER/$tarballName
	perl -pi -e 's/.*### DEBUG.*/###---###/' $TMP_FOLDER/$tarballName/docker-compose.yml
	cp -RL $targetsFolder/$DEV_TARGET_NAME/.env.standalone $TMP_FOLDER/$tarballName
	cp -RL $targetsFolder/$DEV_TARGET_NAME/mx* $TMP_FOLDER/$tarballName
	cp -RL $targetsFolder/$DEV_TARGET_NAME/metaindex.standalone.start.sh $TMP_FOLDER/$tarballName/metaindex.start.sh
	cp -RL $targetsFolder/$DEV_TARGET_NAME/metaindex.stop.sh $TMP_FOLDER/$tarballName/metaindex.stop.sh
	cp -RL $targetsFolder/$DEV_TARGET_NAME/metaindex.restart.sh $TMP_FOLDER/$tarballName/metaindex.restart.sh
	cp -RL $targetsFolder/$DEV_TARGET_NAME/metaindex.common.sh $TMP_FOLDER/$tarballName/metaindex.common.sh	
	cp -RL $targetsFolder/$DEV_TARGET_NAME/metaindex.wsl.start.lnk $TMP_FOLDER/$tarballName/MetaindeX.START.lnk
	cp -RL $targetsFolder/$DEV_TARGET_NAME/metaindex.wsl.stop.lnk $TMP_FOLDER/$tarballName/MetaindeX.STOP.lnk
	cp -RL $targetsFolder/$DEV_TARGET_NAME/metaindex.wsl.restart_webapp.lnk $TMP_FOLDER/$tarballName/MetaindeX.RESTART_WEBAPP.lnk
	cp -RL $targetsFolder/$DEV_TARGET_NAME/metaindex.wsl.config $TMP_FOLDER/$tarballName/wslconfig
	mkdir $TMP_FOLDER/$tarballName/tools
	cp -RL $targetsFolder/$DEV_TARGET_NAME/tools/mx* $TMP_FOLDER/$tarballName/tools
	mkdir $TMP_FOLDER/$tarballName/ssl
	cp -RL $targetsFolder/$DEV_TARGET_NAME/ssl/*.standalone $TMP_FOLDER/$tarballName/ssl

	cd $TMP_FOLDER && zip -qr $DEV_TARBALLS_PATH/$tarballName.zip $tarballName && cd - >/dev/null && rm -rf $TMP_FOLDER/$tarballName

	echo "generated STANDALONE tarball:"
	ls -l $DEV_TARBALLS_PATH/$tarballName.zip

}

WAR_FILE=$DEV_DOCKER_PATH/$DEV_TARGET_NAME/mxwebapp/metaindex.war

if [ ! -f "$WAR_FILE" ]; then
	>&2 echo -e $RED"ERROR : WAR file not reachable : $NORMAL $WAR_FILE"$NORMAL
	exit 1
fi

rm -rf $TMP_FOLDER && mkdir -p $TMP_FOLDER
MX_VERSIONTAG=$(cd $TMP_FOLDER && unzip $WAR_FILE 2>&1 >/dev/null && grep "mx.versiontag=" WEB-INF/classes/metaindex.properties | cut -d= -f 2)
MX_BUILDATE=$(cd $TMP_FOLDER && grep "mx.builddatetag=" WEB-INF/classes/metaindex.properties | cut -d= -f 2)
DEV_MODE=$(cd $TMP_FOLDER && grep "mx.devmode=" WEB-INF/classes/metaindex.properties | cut -d= -f 2)
APP_DATAPACK_NAME=metaindex-$MX_VERSIONTAG
echo "WAR File=$WAR_FILE"
echo "MX_VERSIONTAG=$MX_VERSIONTAG"
echo "MX_BUILDATE=$MX_BUILDATE"
echo "DEV_MODE=$DEV_MODE"

echo
buildStandaloneTarball ${APP_DATAPACK_NAME}".standalone" $DEV_DOCKER_PATH
echo
buildServerTarball ${APP_DATAPACK_NAME}".server" $DEV_DOCKER_PATH


exit 0