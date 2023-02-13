# MetaindeX

MetaindeX is a free open-source cataloger.
It can be seen as a low-cost and accessible way of managing small or heavy well-organized datasets
(i.e. where contents are already clean and arranged by metadata).

It is typically useful if your data is too big for Excel or if you want statistics and plots about its contents.

## Features

MetaindeX has 2 build flavours: standalone and server.
    - Standalone edition is simpler to deploy and allows a single user to work with MetaindeX
    - Server edition needs more configuration setup and is aiming to allow several users to work together on same server

Standalone and Server Edition Features:
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
    - Multi-language (En and Fr available for now)
    - Automatic Quizz Generator
    
Server Edition only:
    - Manage users accounts and sessions
    - Manage Quota
    - Control access rights to catalogs

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

Building metaindex project can only be done from Linux (or W10/WSL) environment:
    - open project under Eclipse
    - export project as a WAR file into <prj_root>/deploy/mxwebapp/metaindex.war
    - run building script: $ cd <prj_root>/deploy && ./tools/devtools/mx_tarball_build.sh

NOTE: for a running Server edition, you'll need to do following additional things:
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
    
    NOTE2: Secrets management is not currently well handled, this shall be improved in upcoming versions.


## Architecture Overview

MetaindeX app is a JE22 WebServer with following components:

![Alt text](relative/path/to/img.jpg?raw=true "Title")

Client Side:
    - HTML5/javascript (jsp) pages
    - Colors styles based on 'less' processing
    - Live communication with server based on websockets 

Server Side:
    - Spring framework
    - MySQL database to store users personnal data and catalogs configuration
    - Elasticsearch to store catalogs contents
    - Apache sshd server for SFTP access to catalog drive
    - Integrated Kibana app for statistics as a self-service
    - 

## References

MetaindeX functionalities and usage has been described through several academical publications:
    - 

