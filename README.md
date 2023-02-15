# MetaindeX

MetaindeX is a free open-source cataloger.
It can be seen as aready-to-deploy application for managing small to heavy datasets.
It supposes though that imported data is already properly organized
(i.e. where contents are already clean and arranged by metadata) and does not offer embedded features to clean your data.

You will typically want to use it if your data is too big for smooth run with Excel or if you want statistics and plots about its contents.

## Features

MetaindeX can be install in 2 versions:
    - Standalone edition: it is simpler to deploy and allows a single user to work with MetaindeX offline (no need to be connected to internet)
    - Server edition: needs more configuration setup and is aiming to allow several users to work together on a central server, generally deployed on internet or intranet of your organization.

Following Features are available in Standalone edition:    
    - Import data from CSV/Excel/OpenOffice
    - Export data to CSV/Gephi
    - Navigate through your data either in cards or table view
    - Modern HTML5/javascript interface
    - Powerful Search Engine
    - Designed to handle heavy contents (100k+ elements)
    - Associate raw documents and web links to your contents (typically pictures) 
    - Handle connections between elements to generate graphs with Gephi
    - Integrates Kibana (from ElasticStack) statistics module
    - Customize interface to your contents
    - Multi-language (English and French available for now)
    - Automatic Quizz Generator
    
Server Edition offers following additional features:
    - Manage users accounts and sessions
    - Manage Quota
    - Control users access rights to catalogs

## Startup Guide

Ready-to-use tarballs are available hereunder.

### System Requirements

    - MetaindeX can be installed either on Linux or Windows (WSL2) systems. 
    - You'll need to have Docker v10.20.21 or greater installed on your system
    - MetaindeX is based on powerful but quite demanding software components (mainly elasticsearch), 
      so it is recommended to have a comfortable hardware configuration to run it smoothly.

### Installation Steps on W10

    - install WSL OS "Debian"    
	- Power shell
	- wsl.exe -d Debian --install
    - download and install latest Docker Desktop application (tested with version 4.16) https://www.docker.com/products/docker-desktop/
    - ensure that Docker demo application runs properly
            
Then you can install and run application:
    - download tarball of latest version (.zip)
    - unzip it in your Documents/metaindex/app folder

#### Start Application on W10
    - if not done yet, start docker Desktop application
    - double-click on MetaindeX.START link (you'll have to restart your computer the first time)

#### Start Application on Linux
    - configure your system to have sufficient memory for elasticsearch: $ sysctl -w vm.max_map_count=262144
    - run following command: $ cd ~/Documents/metaindex/app/<latest version folder> && ./metaindex.start.sh

## Building MetaindeX from sources

Building metaindex project has only been tested under Linux environment:
    - open project under Eclipse
    - export project as a WAR file into <prj_root>/deploy/mxwebapp/metaindex.war
    - run building script: $ cd <prj_root>/deploy && ./tools/devtools/mx_tarball_build.sh

NOTE: for Server edition, you'll need to do following additional things before biulding tarballs:
    - create valid file <prj_root>/deploy/ssl/mxwebapp.server/metaindex.p12
    - create valid file <prj_root>/deploy/ssl/mxproxy.server/metaindex.crt
    - create valid file <prj_root>/deploy/ssl/mxproxy.server/metaindex.key
    - create a <prj_root>/deploy/tools/decode_secrets.sh script displaying following infos on stdout:

        MX_TARGET_KEYSTORE_PASSWORD=xxx
        ELK_METAINDEX_PASSWD=xxx
        ELK_KIBANA_PASSWORD=xxx
        ELK_ELASTIC_PASSWORD=xxx
        MYSQL_PASSWORD=xxx
        MYSQL_ROOT_PASSWORD=xxx
        MX_GOOGLE_MAILER_USER=xxx
        MX_GOOGLE_MAILER_PASSWD=xxx
        MX_ADMIN_MAILER_RECIPIENT=xxx
        MX_PAYMENT_LOGIN=xxx
        MX_PAYMENT_PASSWORD=xxx
    
NOTE2: Secrets management needs still some improvements, and might change in upcoming versions.


## Architecture Overview

MetaindeX app is a JE22 WebServer application, with following components:

![Architeture Overview](doc/visuals/archi.png?raw=true "MetaindeX Architecture Overview")

Client Side:
    - HTML5/javascript (jsp) pages
    - Colors styles based on 'less' processing
    - Live communication with server based on websockets 
    - Integrated Connection to Kibana tool (Elasticstack)
    - SFTP server to access catalogs' drive (mainly useful for server edition, since for standalone edition files can directly be accessed on local file system)

Server Side:
    - Spring framework for access control
    - MySQL database to store users personnal data and catalogs configuration
    - Elasticsearch to store catalogs contents
    - Integrated Kibana app for statistics as a self-service
    - Apache sshd server for SFTP access to catalog drive
    

## References

MetaindeX functionalities and usage has been described through several academical publications:
    - 

