#!/bin/bash

TARGET_SERVER=mxserver
TARGET_FOLDER=mx_deliveries

TARBALLS_DEFAULT_PATH=/Users/laurentml/dev/workspace/mx_deliveries
TARBALL=$1
TARGET_USER=$2
TARGET_PORT=$3


if [ ! -f "$TARBALL" ]; then
	>&2 echo "ERROR : Given TARBALL not reachable : $TARBALL"
	find $TARBALLS_DEFAULT_PATH -maxdepth 1

	exit 1
fi

if [ -z "$TARGET_USER" ]; then
	>&2 echo "ERROR : Missing param user"
	exit 1
fi

if [ -z "$TARGET_PORT" ]; then
	>&2 echo "ERROR : Missing param port"
	exit 1
fi

scp -P $TARGET_PORT $TARBALL $TARGET_USER@$TARGET_SERVER:$TARGET_FOLDER 

exit 0