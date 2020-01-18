#!/bin/bash

TMP_FOLDER=/Users/laurentml/tmp/mx_delivery
DEV_FOLDER=/Users/laurentml/dev/workspace

TARGETS_FOLDER=$DEV_FOLDER/metaindex-core/src/tools/docker/targets
TARGET_NAME=centos7_prod
WAR_FILE=$TARGETS_FOLDER/$TARGET_NAME/metaindex/metaindex-core.war

if [ ! -f "$WAR_FILE" ]; then
	>&2 echo "ERROR : WAR file not reachable : $WAR_FILE"
	exit 1
fi

APP_DATAPACK_FOLDER=$DEV_FOLDER/mx_deliveries

rm -rf $TMP_FOLDER && mkdir -p $TMP_FOLDER
MX_VERSION=$(cd $TMP_FOLDER && unzip $WAR_FILE 2>&1 >/dev/null && grep mx.version WEB-INF/classes/metaindex.properties | cut -d= -f 2)
rm -rf $TMP_FOLDER 

APP_DATAPACK_NAME=metaindex-$TARGET_NAME-$MX_VERSION.tgz

cd $TARGETS_FOLDER && tar Lczf $APP_DATAPACK_FOLDER/$APP_DATAPACK_NAME $TARGET_NAME

echo "generated $APP_DATAPACK_FOLDER/$APP_DATAPACK_NAME"

exit 0