#!/bin/bash


script_path=$(readlink -f $0)
# 'readlink -f' not standard on Mac-OS
if [ "$?" == "0" ]; then
        mx_package_path=$(dirname $script_path)
else
        mx_package_path=$(dirname $0)
fi


function confirm {
	echo -n "	Press enter to continue ..."
	read
	echo
}

VERSION=@VERSION@
TARGET_TOMCAT_PATH=@TARGET_TOMCAT_PATH@
TARGET_SSL_KEYFILE=@TARGET_SSL_KEYFILE@

TIMESTAMP=$(date +%Y%m%d-%H_%M_%S)

BACKUPS_FOLDER=~/backups/metaindex/mx_backup
rm -rf $BACKUPS_FOLDER/
mkdir -p $BACKUPS_FOLDER/tomcat/webapps
mkdir -p $BACKUPS_FOLDER/tomcat/conf

echo "Deploying $VERSION from $mx_package_path"

if [ ! -d "$TARGET_TOMCAT_PATH" ]; then
	>&2 echo "ERROR: given Tomcat target path not reachable : $TARGET_TOMCAT_PATH"
	exit 1
fi


echo " - stopping Tomcat"
confirm
$TARGET_TOMCAT_PATH/bin/shutdown.sh

MX_WAR_FILE=$mx_package_path/tomcat/webapps/metaindex-core.war
MX_SERVER_CONF_PATH=$mx_package_path/tomcat/conf
MX_SSLKEY_PATH=$mx_package_path/sslkey
echo "	- deploying WAR file \""$(ls -l $MX_WAR_FILE)"\" -> $TARGET_TOMCAT_PATH/webapps "
confirm

# cleaning / backup of previous install
mv $TARGET_TOMCAT_PATH/webapps/metaindex* $BACKUPS_FOLDER/tomcat/webapps
if [ "$?" != "0" ]; then
	>&2 echo "ERROR: unable to make backup of current Metaindex WAR file."
	exit 1
fi
# deploying new install
cp $MX_WAR_FILE $TARGET_TOMCAT_PATH/webapps
if [ "$?" != "0" ]; then
	>&2 echo "ERROR: unable to deploy file $MX_WAR_FILE. Backup of previous one is available here : $BACKUPS_FOLDER"
	exit 1
fi

echo "	- deploying Server conf files -> $TARGET_TOMCAT_PATH/conf"
confirm
cp -r $TARGET_TOMCAT_PATH/conf/* $BACKUPS_FOLDER/tomcat/conf
if [ "$?" != "0" ]; then
	>&2 echo "ERROR: unable to make backup of current Metaindex server conf files."
	exit 1
fi
cp $MX_SERVER_CONF_PATH/* $TARGET_TOMCAT_PATH/conf
if [ "$?" != "0" ]; then
	>&2 echo "ERROR: unable to deploy server conf file for version $VERSION. Backup of previous ones is available here : $BACKUPS_FOLDER"
	exit 1
fi

echo "	- deploying SSL-Key file -> $TARGET_SSL_KEYFILE"
confirm
cp $MX_SSLKEY_PATH/* $TARGET_SSL_KEYFILE
if [ "$?" != "0" ]; then
	>&2 echo "ERROR: unable to deploy SSL-Key file for version $VERSION. Backup of previous ones is available here : $BACKUPS_FOLDER"
	exit 1
fi

echo "	- zipping previous conf backup as '$BACKUPS_FOLDER/../metaindex_backup_$TIMESTAMP.zip'"
confirm
echo -n "ZIP encryption : " 
cd $BACKUPS_FOLDER/.. && zip -re --quiet metaindex_backup_$TIMESTAMP.zip mx_backup && rm -rf $BACKUPS_FOLDER
if [ "$?" != "0" ]; then
	>&2 echo "ERROR: unable to deploy server conf file for version $VERSION. Backup of previous ones is available here : $BACKUPS_FOLDER"
	exit 1
fi

echo " - restarting Tomcat ... Press enter to confirm"
confirm
$TARGET_TOMCAT_PATH/bin/startup.sh

exit 0



