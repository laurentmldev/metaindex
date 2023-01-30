#!/bin/bash

script_name=$(basename $0)
script_dir=$(dirname $0)

ELK_DB_NAME="metaindex"
MX_SERVER_USER=root
source $script_dir/../mx_tools_commons.sh

MYSQL_PWD=
CATALOG_IDS_LIST=
function usage() {
	echo "$script_name passwd [backup_id]"
	echo "	extract contents of MX mysql database"
	echo " OPTIONS:"
	echo "	-p <passwd>: mysql password"
	echo "  -c <catalog_id1,catalog_id12,catalog_id3>: limit data to given catalogs"
}

while getopts "hp:c:" option; do
		case ${option} in
			h) usage;exit 1;; # $OPTARG
			p) MYSQL_PWD=$OPTARG;; 
			c) CATALOG_IDS_LIST=$OPTARG;;
		esac
done
shift $((OPTIND -1))


if [ -z "$MYSQL_PWD" ]; then
	echo -n "SQL-DB password: " 
	read -s MYSQL_PWD
	if [ -z "$MYSQL_PWD" ]; then
		>&2 echo -e $ERROR"ERROR: given MySQL password is empty, aborting."$NORMAL
		exit 1
	fi
fi

BACKUP_NAME=$1
if [ -z "$BACKUP_NAME" ]; then
	BACKUP_NAME="mx-backup_$TIMESTAMP"
fi

if [ ! -z "$CATALOG_IDS_LIST" ]; then
	CATALOG_IDS_LIST=$(echo $CATALOG_IDS_LIST | sed  "s/,/ /g")
fi


sqlFile=$MX_SERVER_BACKUPS_PATH/$BACKUP_NAME.sql

if [ -z "$CATALOG_IDS_LIST" ]; then
	echo -n "extracting fully total and global MYSQL contents ..."
	mysqldump --password=$MYSQL_PWD $ELK_DB_NAME > $sqlFile
	if [ "$?" != "0" ]; then
		>&2 echo -e $ERROR"	FAILED"$NORMAL
		exit 1
	fi
else
	nbCatalogs=$(echo "$CATALOG_IDS_LIST" | wc -w)
	echo -n "extracting MYSQL contents for $nbCatalogs catalog(s) ..."
	SQLDUMPOPTIONS="--password=$MYSQL_PWD  --skip-add-drop-table --dump-date --replace"		

	for catalog_id in $CATALOG_IDS_LIST; do
		echo "	### Catalog $catalog_id ###"
		# catalogs 
		mysqldump $SQLDUMPOPTIONS --where="catalog_id=$catalog_id COLLATE utf8_general_ci" $ELK_DB_NAME catalogs 2>/dev/null | sed 's%^CREATE%/*CREATE%g' | sed -E 's%( ENGINE=.*)%\1*/%g' > $sqlFile 
		# catalogs_perspectives
		mysqldump $SQLDUMPOPTIONS --where="catalog_id=$catalog_id"  $ELK_DB_NAME catalogs_perspectives 2>/dev/null | sed 's%^CREATE%/*CREATE%g' | sed -E 's%( ENGINE=.*)%\1*/%g' >> $sqlFile
		# catalogs_terms
		mysqldump $SQLDUMPOPTIONS --where="catalog_id=$catalog_id"  $ELK_DB_NAME catalog_terms 2>/dev/null | sed 's%^CREATE%/*CREATE%g' | sed -E 's%( ENGINE=.*)%\1*/%g' >> $sqlFile
		# catalog_terms_vocabulary 
		mysqldump $SQLDUMPOPTIONS --where="catalog_term_id in (select catalog_term_id from catalog_terms where catalog_id=$catalog_id)"  $ELK_DB_NAME catalog_terms_vocabulary 2>/dev/null | sed 's%^CREATE%/*CREATE%g' | sed -E 's%( ENGINE=.*)%\1*/%g' >> $sqlFile
		# catalog_vocabulary 
		mysqldump $SQLDUMPOPTIONS --where="catalog_id=$catalog_id"  $ELK_DB_NAME catalog_vocabulary 2>/dev/null | sed 's%^CREATE%/*CREATE%g' | sed -E 's%( ENGINE=.*)%\1*/%g' >> $sqlFile
		# filters 
		mysqldump $SQLDUMPOPTIONS --where="catalog_id=$catalog_id"  $ELK_DB_NAME filters 2>/dev/null | sed 's%^CREATE%/*CREATE%g' | sed -E 's%( ENGINE=.*)%\1*/%g' >> $sqlFile
		# user_catalogs_customization 
		mysqldump $SQLDUMPOPTIONS --where="catalog_id=$catalog_id"  $ELK_DB_NAME user_catalogs_customization 2>/dev/null | sed 's%^CREATE%/*CREATE%g' | sed -E 's%( ENGINE=.*)%\1*/%g' >> $sqlFile
		# user_catalogs_rights 
		mysqldump $SQLDUMPOPTIONS --where="catalog_id=$catalog_id"  $ELK_DB_NAME user_catalogs_rights 2>/dev/null | sed 's%^CREATE%/*CREATE%g' | sed -E 's%( ENGINE=.*)%\1*/%g' >> $sqlFile
		# 
	done
fi

echo "SQL dump generated :"
ls -l $sqlFile


exit 0
