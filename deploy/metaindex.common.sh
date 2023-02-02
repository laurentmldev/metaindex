#!/bin/bash


DOCKER_ENV_ROOT=".env"
WAIT_STARTUP_SEC=600
    
MODULES_LIST="mxwebapp mxproxy mxmysql mxelsrc mxkibana mxpma"
VOLUMES_LIST="mxmysql-data mxmysql-backups mxelsrc-data mxelsrc-config mxelsrc-logs mxelsrc-backups"
WSL_WIN_USERSPATH="/mnt/c/Users"
function checkWslConfig() {

    isWsl=0
    userHomeFolder=$(id -un)
    
    # check if we are in Windows (WSL) env
    if [ ! -d "$WSL_WIN_USERSPATH" ]; then
        return
    fi

    if [ -z "$WINHOME" ]; then     
        WINHOME=$(wslpath  "$(wslvar USERPROFILE)")   
        if [ -z "$WINHOME" ] || [ ! -d "$WINHOME" ]; then
            >&2 echo "ERROR: unable to find Windows Home folder within $WINHOME."            
            exit 1
        fi
    fi

    echo "[creating Windows WSL config within $WINHOME]"
    
    wslconfig_target=$WINHOME/.wslconfig
    wslconfig_source=$(dirname $0)/wslconfig
    if [ ! -f "$wslconfig_target" ]; then
        cp $wslconfig_source $wslconfig_target
        if [ "$?" != "0" ]; then
            >&2 echo "ERROR: unable to create wslconfig file $wslconfig_target, aborting."
            exit 1
        fi   
    else
        diff $wslconfig_target $wslconfig_source >/dev/null
        if [ "$?" != "0" ]; then
            >&2 echo "Existing wslconfig file ($wslconfig_target) differs from expected one ($wslconfig_source), please report contents from into it and start again."
            exit 1
        fi   
    fi

    mkdir -p $WINHOME/Documents/metaindex/app
    if [ "$?" != "0" ]; then
        >&2 echo "ERROR: unable to create catalogs drive as $WINHOME/Documents/metaindex, aborting."
        exit 1
    fi

    perl -pi -e "s%MX_USERDATA_HOSTPATH=.*%MX_USERDATA_HOSTPATH=$WINHOME/Documents/metaindex%" .env.standalone
    if [ "$?" != "0" ]; then
        >&2 echo "ERROR: unable to fix catalogs drive path into file .env.standalone, aborting."
        exit 1
    else
        echo "Catalogs files contents will be in your Documents/metaindex/<catalog> folder."
    fi

}

function create_volumes() {
    existVolumes=$(docker volume ls | grep local | sed -E 's%local +%%')
    for vol in $VOLUMES_LIST; do
        echo "$existVolumes" | egrep "^$vol\$" >/dev/null
        if [ "$?" != "0" ]; then
            echo "[creating missing volume $vol]"
            docker volume create --name=$vol >/dev/null
            if [ "$?" != "0" ]; then
                >&2 echo "ERROR: sorry unable to create volume $vol, aborting."
                exit 1
            fi
        fi
    done
}

function showRunningStatus() {

    missingModules=""
    runningModules=""
    dockerList=$(docker ps)

    for module in $MODULES_LIST; do
        echo "$dockerList" | grep  $module >/dev/null
        if [ "$?" == "1" ]; then 
            missingModules=$missingModules"$module ";
        else  
            runningModules=$runningModules"$module ";
        fi
    done
    
    if [ -z "$missingModules" ]; then
        echo "All modules are up."
        return 0
    fi
    if [ -z "$runningModules" ]; then
        echo "App is currently down."
        return 1
    fi

    echo "Running modules: $runningModules"
    echo "Missing modules: $missingModules"
    return 1
}


progressBarSpinIdx=0
function progressBar() {

    nbDone=$1
    nbTotal=$2
    title=$3
    comment=$4
    spin[0]="-"
    spin[1]="\\"
    spin[2]="|"
    spin[3]="/"
    
    if [ -z "$title" ]; then title="Progress"; fi
    
    let _progress=(${nbDone}*100/${nbTotal}*100)/100
    let _done=(${_progress}*4)/10
    let _left=40-$_done

    _fill=$(printf "%${_done}s")
    _empty=$(printf "%${_left}s")

    spinChar="${spin[$progressBarSpinIdx]}"
    if [ "$nbDone" == "$nbtotal" ]; then spinChar=" "; fi

    spinChar=" "
    if [ "$_progress" != "100" ]; then spinChar=${spin[$progressBarSpinIdx]}; fi
    printf "\r$title : [${_fill// /#}${_empty// /-}] ${_progress}%% $spinChar $comment" 
}


mxproxy_curstep=0
mxproxy_totalsteps=1
mxproxy_comment=
mxproxy_logs_start_line=$(( $(docker logs mxproxy 2>&1 | wc -l) + 1 ))
function checkForProxyReady() {    
    
    docker ps | grep mxproxy >/dev/null
    if [ "$?" != "0" ]; then
        mxproxy_comment="STOPPED"
        mxproxy_curstep="-1"
        docker ps
        return -1
    fi

    if [ "$mxproxy_curstep" != "$mxproxy_totalsteps" ]; then
        docker logs mxproxy  2>&1 | tail -n +$mxproxy_logs_start_line | egrep -e 'httpd -D FOREGROUND' >/dev/null
        if [ "$?" == "0" ]; then
            mxproxy_comment="Proxy READY"
            mxproxy_curstep=$mxproxy_totalsteps
            return 0
        fi 
    fi

    return 1
    
}


mxmysql_versionFound=
mxmysql_starting=
mxmysql_initialized=
mxmysql_curstep=0
mxmysql_totalsteps=4
mxmysql_comment=
mxmysql_logs_start_line=$(( $(docker logs mxmysql 2>&1 | wc -l) + 1 ))
function checkForMySqlReady() {    
    
    docker ps | grep mxmysql >/dev/null
    if [ "$?" != "0" ]; then
        mxmysql_comment="STOPPED"
        mxmysql_curstep="-1"
        return -1
    fi
    
    if [ -z "$mxmysql_versionFound" ]; then
        docker logs mxmysql  2>&1  | tail -n +$mxmysql_logs_start_line | egrep -e 'starting as process' >/dev/null
        if [ "$?" == "0" ]; then
            version=$(docker logs mxmysql 2>&1 | egrep -e 'starting as process' | perl -p -e 's/.*mysqld \(mysqld (.*)\).*/$1/')
            mxmysql_comment="MySQL v$version"
            mxmysql_versionFound=1
            mxmysql_curstep=1
        fi
    fi

    if [ -z "$mxmysql_starting" ]; then
        docker logs mxmysql  2>&1 | tail -n +$mxmysql_logs_start_line | egrep -e 'InnoDB initialization has ended' >/dev/null
        if [ "$?" == "0" ]; then
            mxmysql_comment="starting"
            mxmysql_starting=1
            mxmysql_curstep=2
        fi 
    fi

    if [ -z "$mxmysql_initialized" ]; then
        docker logs mxmysql  2>&1 | tail -n +$mxmysql_logs_start_line | egrep -e 'MySQL init process done. Ready for start up' >/dev/null
        if [ "$?" == "0" ]; then
            mxmysql_comment="initialized. Starting over."
            mxmysql_initialized=1
            mxmysql_curstep=3
        fi 
    fi

    if [ "$mxmysql_curstep" != "$mxmysql_totalsteps" ]; then
        docker logs mxmysql  2>&1 | tail -n +$mxmysql_logs_start_line | egrep -e 'mysqld: ready for connections' >/dev/null
        if [ "$?" == "0" ]; then
            mxmysql_comment="MySQL READY"
            mxmysql_curstep=$mxmysql_totalsteps
            return 0
        fi 
    fi

    return 1
    
}


mxelsrc_modulesLoaded=
mxelsrc_versionFound=
mxelsrc_initialized=
mxelsrc_curstep=0
mxelsrc_totalsteps=4
mxelsrc_comment=
mxelsrc_logs_start_line=$(( $(docker logs mxelsrc 2>&1 | wc -l) + 1 ))
function checkForElasticsearchReady() {    
    
    docker ps | grep mxelsrc >/dev/null
    if [ "$?" != "0" ]; then
        mxelsrc_comment="STOPPED"
        mxelsrc_curstep="-1"
        return -1
    fi
    
    if [ -z "$mxelsrc_modulesLoaded" ]; then
        docker logs mxelsrc  2>&1 | tail -n +$mxelsrc_logs_start_line | egrep -e 'loaded module' >/dev/null
        if [ "$?" == "0" ]; then
            mxelsrc_comment="modules loaded"
            mxelsrc_modulesLoaded=1
            mxelsrc_curstep=1
        fi 
    fi

    if [ -z "$mxelsrc_versionFound" ]; then
        docker logs mxelsrc  2>&1 | tail -n +$mxelsrc_logs_start_line | egrep -e '\[controller' >/dev/null
        if [ "$?" == "0" ]; then
            version=$(docker logs mxelsrc 2>&1 | egrep -e '\[controller' | perl -p -e 's/.*Version (\d+\.\d+\.\d+).*/$1/')
            mxelsrc_comment="Elasticsearch v$version"
            mxelsrc_versionFound=1
            mxelsrc_curstep=2
        fi
    fi

    if [ -z "$mxelsrc_initialized" ]; then
        docker logs mxelsrc  2>&1 | tail -n +$mxelsrc_logs_start_line | egrep -e '"initialized"' >/dev/null
        if [ "$?" == "0" ]; then
            mxelsrc_comment="init done, starting service"
            mxelsrc_initialized=1
            mxelsrc_curstep=3
        fi 
    fi

    if [ "$mxelsrc_curstep" != "$mxelsrc_totalsteps" ]; then
        docker logs mxelsrc  2>&1 | tail -n +$mxelsrc_logs_start_line | egrep -e ' to \[(YELLOW|GREEN)\]' >/dev/null
        if [ "$?" == "0" ]; then
            mxelsrc_comment="Elasticsearch READY"
            mxelsrc_curstep=$mxelsrc_totalsteps
            return 0
        fi 
        
    fi

    return 1
    
}

mxkibana_httpServerUp=
mxkibana_configDone=
mxkibana_savedObjects=
mxkibana_curstep=0
mxkibana_totalsteps=5
mxkibana_comment=
mxkibana_logs_start_line=$(( $(docker logs mxkibana 2>&1 | wc -l) + 1 ))
function checkForKibanaReady() {
    
    docker ps | grep mxkibana >/dev/null
    if [ "$?" != "0" ]; then
        mxkibana_comment="STOPPED"
        mxkibana_curstep="-1"
        return -1
    fi

    if [ -z "$mxkibana_httpServerUp" ]; then
        docker logs mxkibana  2>&1 | tail -n +$mxkibana_logs_start_line | egrep -e 'config sourced from' >/dev/null
        if [ "$?" == "0" ]; then
            mxkibana_comment="config done"
            mxkibana_configDone=1
            mxkibana_curstep=1
        fi 
    fi
    if [ -z "$mxkibana_httpServerUp" ]; then
        docker logs mxkibana  2>&1 | tail -n +$mxkibana_logs_start_line | egrep -e 'http server running at' >/dev/null
        if [ "$?" == "0" ]; then
            mxkibana_comment="http server UP"
            mxkibana_httpServerUp=1
            mxkibana_curstep=2
        fi 
    fi

    if [ "$mxkibana_savedObjects" == "1" ]; then
        docker logs mxkibana  2>&1 | tail -n +$mxkibana_logs_start_line | egrep -e 'Migration completed' >/dev/null
        if [ "$?" == "0" ]; then
            mxkibana_comment="saved objects restored"
            mxkibana_savedObjects=2
            mxkibana_curstep=4
        fi
    fi

    if [ -z "$mxkibana_savedObjects" ]; then
        docker logs mxkibana  2>&1 | tail -n +$mxkibana_logs_start_line | egrep -e 'Starting saved objects migrations' >/dev/null
        if [ "$?" == "0" ]; then
            mxkibana_comment="restoring saved objects"
            mxkibana_savedObjects=1
            mxkibana_curstep=3
        fi
    fi

    if [ "$mxkibana_curstep" != "$mxkibana_totalsteps" ]; then
        docker logs mxkibana  2>&1 | tail -n +$mxkibana_logs_start_line | egrep -e 'Kibana is now available' >/dev/null
        if [ "$?" == "0" ]; then
            mxkibana_comment="Kibana READY"            
            mxkibana_curstep=$mxkibana_totalsteps
            return 0
        fi 
    fi
    
    return 1
}


mxwebapp_startingEngine=
mxwebapp_initializing=
mxwebapp_curstep=0
mxwebapp_totalsteps=3
mxwebapp_comment=
mxwebapp_logs_start_line=$(( $(docker logs mxwebapp 2>&1 | wc -l) + 1 ))
function checkForWebAppReady() {    
    
    docker ps | grep mxwebapp >/dev/null
    if [ "$?" != "0" ]; then
        mxwebapp_comment="STOPPED"
        mxwebapp_curstep="-1"
        return -1
    fi

    if [ -z "$mxwebapp_startingEngine" ]; then
        docker logs mxwebapp  2>&1 | tail -n +$mxwebapp_logs_start_line | egrep -e 'Starting Servlet engine' >/dev/null
        if [ "$?" == "0" ]; then
            mxwebapp_comment="starting servlet engine"
            mxwebapp_startingEngine=1
            mxwebapp_curstep=1
        fi
    fi

    if [ -z "$mxwebapp_initializing" ]; then
        docker logs mxwebapp  2>&1 | tail -n +$mxwebapp_logs_start_line | egrep -e ' MetaindeX v' >/dev/null
        if [ "$?" == "0" ]; then
            version=$(docker logs mxwebapp 2>&1 | tail -n +$mxwebapp_logs_start_line | egrep -e ' MetaindeX v' | perl -p -e 's/.*v(\d+\.\d+\.\d+)\.(\w+).*/$1 ($2)/')
            mxwebapp_comment="initializing v$version"
            mxwebapp_initializing=1
            mxwebapp_curstep=2
        fi
    fi

    if [ "$mxwebapp_curstep" != "$mxwebapp_totalsteps" ]; then
        docker logs mxwebapp  2>&1 | tail -n +$mxwebapp_logs_start_line | egrep -e 'Server startup in' >/dev/null
        if [ "$?" == "0" ]; then
            mxwebapp_comment="WebApp READY"
            mxwebapp_curstep=$mxwebapp_totalsteps
            return 0
        fi 
    fi

    return 1
    
}

function start_mx_server() {

    startWebApp=1
    startBackApps=1
    if [ "$1" == "noWebApp" ]; then
        startWebApp=0
    elif [ "$1" == "onlyWebApp" ]; then
        startBackApps=0
    fi

    showRunningStatus
    if [ "$?" == "0" ]; then
        return 0
    fi

    create_volumes
    if [ "$startBackApps" == "1" ]; then

    docker-compose --env-file $DOCKER_ENV_ROOT.current up -d mxproxy
    if [ "$?" != "0" ]; then
        >&2 echo "ERROR: unable to start MetaindeX component mxproxy, sorry"
        exit 1
    fi

    NB_PROGRESS_BARS=4
    echo
    echo "waiting for components to wake-up (might take few minutes): "
    echo
    progressBar 0 $mxproxy_totalsteps "Proxy" "starting ...                        "
    echo    
    progressBar 0 $mxmysql_totalsteps "MySQL" "starting ...                        "
    echo    
    progressBar 0 $mxelsrc_totalsteps "Elasticsearch" "starting ...                        "
    echo    
    progressBar 0 $mxkibana_totalsteps "Kibana" "starting ...                         "


    for i in $(seq 1 $WAIT_STARTUP_SEC); do    
        sleep 0.3
        checkForProxyReady
        checkForMySqlReady
        checkForElasticsearchReady
        checkForKibanaReady
        progressBarSpinIdx=$(($i % 4))
        echo -e "\033[${NB_PROGRESS_BARS}A"
        progressBar $mxproxy_curstep $mxproxy_totalsteps "Proxy" "$mxproxy_comment                         "
        echo    
        progressBar $mxmysql_curstep $mxmysql_totalsteps "MySQL" "$mxmysql_comment                         "
        echo    
        progressBar $mxelsrc_curstep $mxelsrc_totalsteps "Elasticsearch" "$mxelsrc_comment                         "
        echo    
        progressBar $mxkibana_curstep $mxkibana_totalsteps "Kibana" "$mxkibana_comment                                       "
        
        if [ "$mxkibana_curstep" == "$mxkibana_totalsteps" ] \
        && [ "$mxelsrc_curstep" == "$mxelsrc_totalsteps" ] \
        && [ "$mxmysql_curstep" == "$mxmysql_totalsteps" ]; then
            break
        fi

        if [ "$mxkibana_curstep" == "-1" ] \
        || [ "$mxproxy_curstep" == "-1" ] \
        || [ "$mxelsrc_curstep" == "-1" ] \
        || [ "$mxmysql_curstep" == "-1" ]; then
            echo
            >&2  echo -n "ERROR: some elements could not start, quitting (Y/n) ? "
            read answer
            if [ "$answer" != "n" ]; then
                docker-compose --env-file $DOCKER_ENV_ROOT.current down
            fi
            exit 1
        fi
        
    done
    echo
    echo
    fi

    if [ "$startWebApp" == "1" ]; then

        docker-compose --env-file $DOCKER_ENV_ROOT.current up -d mxwebapp
        if [ "$?" != "0" ]; then
            >&2 echo "ERROR: unable to start MetaindeX component mxwebapp, sorry"
            exit 1
        fi
        echo
        progressBar 0 $mxwebapp_totalsteps "WebApp" "starting ...                        "
        for i in $(seq 1 $WAIT_STARTUP_SEC); do    
            sleep 0.3
            checkForWebAppReady
            progressBarSpinIdx=$(($i % 4))
            echo -e "\033[1A"
            progressBar $mxwebapp_curstep $mxwebapp_totalsteps "WebApp" "$mxwebapp_comment                         "
            
            if [ "$mxwebapp_curstep" == "$mxwebapp_totalsteps" ]; then
                break
            fi
            
        done
        echo
    else
        echo "[Skipped start of WebApp]"
    fi
    echo

    showRunningStatus

}