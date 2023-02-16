# MetaindeX

MetaindeX is a free open-source and ready-to-deploy cataloger application: it allows you to create, edit, consult and search within small to heavy datasets.

You can import data from Excel, ODF or CSV files, supposing that contents are clean (i.e. not ambiguous).

You will typically want to use it if your data is too big for smooth run with Excel or if you want statistics and plots about its contents.

![Screenshot 1](src/main/webapp/public/commons/media/img/screenshots/cards1.png?raw=true "Cards View screenshot")
![Screenshot 2](src/main/webapp/public/commons/media/img/screenshots/stats-map.png?raw=true "Stats Map screenshot")
![Screenshot 3](src/main/webapp/public/commons/media/img/screenshots/stats.png?raw=true "Stats Plots screenshot")

To help you cleaning your data, you can first use [OpenRefine](https://openrefine.org) tool. If this is not suffiscient, you will probably need to go into some code. Few python tools are available [here](src/main/webapp/public/toolbox) which could help you in this task.

## Features

MetaindeX can be install in 2 versions:
* **Standalone edition:** it is simpler to deploy and allows a single user to work with MetaindeX offline (no need to be connected to internet except for tutorial videos)
* **Server edition**: needs more configuration setup and is aiming to allow several users to work together on a central server, generally deployed on internet or intranet of your organization.

Available Features are synthetized in following table:

|Feature|Standalone Edition|Server Edition|
|-------|------------------|--------------|
|Import data from CSV/Excel/OpenOffice|  :heavy_check_mark:  | :heavy_check_mark:  |
|Export data to CSV/Gephi| :heavy_check_mark: | :heavy_check_mark: |
|Cards & Table Navigation Mode| :heavy_check_mark: | :heavy_check_mark: |
|Lightweight client HTML5/javascript interface| :heavy_check_mark: | :heavy_check_mark: |
|Powerful Search Engine (lucene)| :heavy_check_mark: | :heavy_check_mark: |
|Robust to heavy contents (100k+ elements)| :heavy_check_mark: | :heavy_check_mark: |
|Include raw data (ex:pictures) and external web links to documents| :heavy_check_mark: | :heavy_check_mark: |
|Build connections between elements to generate graphs with Gephi| :heavy_check_mark: | :heavy_check_mark: |
|Integrates Kibana (from ElasticStack) statistics module| :heavy_check_mark: | :heavy_check_mark: |
|Easy interface customization to match your data model | :heavy_check_mark: | :heavy_check_mark: |
|Multi-language (English and French available for now)| :heavy_check_mark: | :heavy_check_mark: |
|Automatic Quizz Generator| :heavy_check_mark: | :heavy_check_mark: |
|Manage users accounts and sessions|  :x:  | :heavy_check_mark: |
|Manage Quota|  :x:  | :heavy_check_mark: |
|Control users access rights to catalogs|  :x:  | :heavy_check_mark: |
|live chat with other users|  :x:  | :heavy_check_mark: |



## Startup Guide

### System Requirements

* MetaindeX can be installed either on Linux or Windows (WSL2) systems. 
* You'll need to have Docker v10.20.21 or greater installed on your system
* MetaindeX is based on powerful but quite demanding software components (mainly elasticsearch), so it is recommended to have a comfortable hardware configuration to run it smoothly.

### Installation Steps on W10

* Open a "Power shell" and install WSL OS "Debian" :
```
$ wsl.exe -d Debian --install
```
* download and install latest [Docker Desktop application](https://www.docker.com/products/docker-desktop/) (tested with version 4.16) 
* ensure that Docker demo application runs properly
            
Then you can install and run MetaindeX application:
* download tarball of latest version (.zip) (see tarballs section hereunder)    
* unzip it in your Documents/metaindex/app folder

#### Start Application on W10
*  if not done yet, start docker Desktop application
*  double-click on MetaindeX.START link (the first time you'll have to restart your computer)

#### Start Application on Linux
*  configure your system to have sufficient memory for elasticsearch: 
```
$ sysctl -w vm.max_map_count=262144
```
*  run following command: 
```
$ cd ~/Documents/metaindex/app/<latest version folder> && ./metaindex.start.sh
```

## Building MetaindeX from sources

Building metaindex project has only been tested under Linux environment:
*  open project under Eclipse
*  export project as a WAR file into <prj_root>/deploy/mxwebapp/metaindex.war
*  run building script: 
```
$ cd <prj_root>/deploy && ./tools/devtools/mx_tarball_build.sh
```

NOTE: for Server edition, you'll need to do following additional things before biulding tarballs:
*  create valid file <prj_root>/deploy/ssl/mxwebapp.server/metaindex.p12
*  create valid file <prj_root>/deploy/ssl/mxproxy.server/metaindex.crt
*  create valid file <prj_root>/deploy/ssl/mxproxy.server/metaindex.key
*  create a <prj_root>/deploy/tools/decode_secrets.sh script displaying following infos on stdout (default values are available in deploy/.env.standalone file):
```
# p12 file password
MX_TARGET_KEYSTORE_PASSWORD=xxx
# password to be used for 'metaindex' admin user in elasticsearch
ELK_METAINDEX_PASSWD=xxx
# password to be used for 'kibana' user in elasticsearch
ELK_KIBANA_PASSWORD=xxx
# password to be used for 'elastic' super user in elasticsearch
ELK_ELASTIC_PASSWORD=xxx
# password to be used for 'mxsql' user in mysql DB
MYSQL_PASSWORD=xxx
# password to be used for 'root' user in mysql DB
MYSQL_ROOT_PASSWORD=xxx
# to receive email notifications, you must have a gmail account configured for being used by a third-party app'
MX_GOOGLE_MAILER_USER=xxx
# corresponding gmail password (API app password, not your personnal human password)
MX_GOOGLE_MAILER_PASSWD=xxx
# email address to be used to send metaindex notifications
MX_ADMIN_MAILER_RECIPIENT=xxx
# payement API login (only paypal interface implemented for now)
MX_PAYMENT_LOGIN=xxx
# payement API password
MX_PAYMENT_PASSWORD=xxx
```

Management of thoses secrets still needs some improvements, and might change in upcoming versions.

## Taeballs

**Standalone Edition**
* LATEST: [MetaindeX Standalone v3.0.2 (.zip)](https://imagingyou.me/metaindex/metaindex-3.0.2.RELEASE.standalone.zip) SHA256:  

**Server Edition**
* LATEST: [MetaindeX Standalone v3.0.2 (.tgz)](https://imagingyou.me/metaindex/metaindex-3.0.2.RELEASE.server.tgz) SHA256:  

## Architecture Overview

MetaindeX app is a JE22 WebServer application with a basic MVC approach, synthetized in following figure:

![Architeture Overview Figure](doc/visuals/archi.png?raw=true "MetaindeX Architecture Overview")

Client Side:
*  HTML5/javascript pages, no special framework used (might be part of a future evolution)
*  Colors styling based on 'less' processing
*  Live communication with server based on websockets 
*  Integrated Connection to Kibana tool (Elasticstack)
*  SFTP server to access catalogs' drive (mainly useful for server edition, since for standalone edition files can directly be accessed on local file system)

Server Side:
*  Spring framework for access control
*  MySQL database to store users personnal data and catalogs configuration
*  Elasticsearch to store catalogs contents
*  Integrated Kibana app for statistics as a self-service
*  Apache sshd server for SFTP access to catalog drive

## References

MetaindeX functionalities and usage has been described through some academical publications:
*  [HistoInformatics 2021](https://ceur-ws.org/Vol-2981/):
    * [Specifying a Generic Working Environment on Historical Data, based on MetaindeX, Kibana and Gephi](https://ceur-ws.org/Vol-2981/paper1.pdf)
* [Link Archives 2021](https://ceur-ws.org/Vol-3019/):
    * [ A Ready-to-Use Solution to Explore Linked Archives with MetaindeX and Gephi](https://ceur-ws.org/Vol-3019/LinkedArchives_2021_paper_8.pdf)
* [FOSDEM 2022](https://archive.fosdem.org/2022/): 
    * [MetaindeX and user requirements for a generic catalog application](https://archive.fosdem.org/2022/schedule/event/open_research_metaindex/)
    * [![Video Presentation](https://video.fosdem.org/2022/D.research/open_research_metaindex.webm)]

