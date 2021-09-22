#!/bin/bash
echo ----start prepare dataloader----
touch temp1
./encrypt.sh -e $1 ./conf/mykey >temp1
haslo1=`tail -1 temp1`
echo -----start-haslo-----
echo $haslo1
echo -----end-haslo-------
login1=$2
# add parametr for example: test.salesforce.com
environment=`echo $3|sed 's/https\:\/\///g'`
#name environment
name_env="$4"
mydirectory=$5
name_package=$6
#parameter -z check that variable is empty
if [ -z "${login1}" ] || [ -z "{haslo1}" ]; then
	echo "!!!!!! Missing login or password !!!!!!"
	exit 1;
fi
sequenceObject=''
for katalog in `ls sdlFiles`
do

    if [ "delete" == "$katalog" ]; then
        sequenceObject='deleteSequenceRunObject'
    else
        sequenceObject='sequenceRunObject'
    fi

	for obj in `cat $sequenceObject`
	do
	    sdl1=`ls sdlFiles/$katalog|grep sdl| grep $obj`
	    if [ "${sdl1}" == "${obj}.sdl" ]; then
            echo sdl $sdl1
            touch tmp
            cut -d '=' -f2 sdlFiles/$katalog/$sdl1>tmp
            sed -i '/\#/d' tmp
            dos2unix tmp
            bodyselect=`cat tmp| tr '\n' ', '`
            bodyselect=`echo ${bodyselect%?}|sed 's/\\\:/./g'`
            object=`echo $sdl1|sed 's/\.sdl//g'`
            select1="Select $bodyselect from $object"
            touch conf/config.properties
            echo "sfdc.username=$login1">conf/config.properties
            echo "sfdc.password=$haslo1">>conf/config.properties
            echo "sfdc.endpoint=https\://$environment">>conf/config.properties
            echo "process.encryptionKeyFile=$mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/conf/mykey">>conf/config.properties
            cat part1_config.properties>>conf/config.properties
            echo "sfdc.oauth.redirecturi=https\://$environment">>conf/config.properties
            echo "sfdc.oauth.server=https\://$environment">>conf/config.properties
            echo "process.operation=$katalog">>conf/config.properties
            echo "sfdc.entity=$object">>conf/config.properties
            csvfile=`ls sdlFiles/$katalog/$object*|grep csv`
            if [ -z "$csvfile" ] && [ "$katalog" != "extract" ];then
                echo "!!!!!! Missing CSV file !!!!!!"
                exit 1;
            fi
            if [ "$katalog" == "extract" ]; then
                if [ ! -d $name_env ]; then
                    mkdir $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$name_env
                    chmod 777 $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$name_env
                    mkdir $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$name_env/sdlFiles
                    chmod 777 $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$name_env/sdlFiles
                    mkdir $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$name_env/sdlFiles/extract
                    chmod 777 $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$name_env/sdlFiles/extract
                    touch $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$name_env/sdlFiles/extract/.emptydir
                    touch $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$name_env/sdlFiles/extract/${object}.csv
                 fi
                touch $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$name_env/$csvfile
                echo "dataAccess.name=$mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$name_env/$csvfile">>conf/config.properties
            else
                echo "dataAccess.name=$mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/$csvfile">>conf/config.properties
            fi

            typeAccess=''
            current_date=`date +%F`
            namedate=`echo "_csvfile_$current_date"`
            name_success="${name_env}_${object}_${namedate}_CsvSuccess.csv"
            name_error="${name_env}_${object}_${namedate}_CsvError.csv"
            if [ "$katalog" == "extract" ]; then
                typeAccess='csvWrite'
            else
                typeAccess='csvRead'
            fi
            echo "dataAccess.type=$typeAccess">>conf/config.properties
            echo "sfdc.extractionSOQL=$select1">>conf/config.properties
            externalId=''
            if [ "$katalog" == "upsert" ] || [ "$katalog" == "update" ]; then
                externalId=`cat object_external_id.txt | grep $object|cut -d '=' -f2`

                if [ -z "${externalId}" ]; then
                    echo "!!!!!!!! Missing extrernal Id !!!!!!!!!!"
                    exit 1;
                fi
                echo "sfdc.externalIdField=$externalId">>conf/config.properties
            fi
            echo "process.mappingFile=sdlFiles/$katalog/$sdl1">>conf/config.properties

            echo "process.statusOutputDirectory=$mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$katalog">>conf/config.properties
            echo "process.outputSuccess=$mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$katalog/$name_success">>conf/config.properties
            echo "process.outputError=$mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status/$katalog/$name_error">>conf/config.properties
            chmod 666 $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/conf/*
            ./process.sh $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/conf/
            cat $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/conf/config.properties
        fi
	done
	echo katalog $katalog
	sleep 2s
done
file_package="package"
if [ ! -d $file_package ]; then
    mkdir $file_package
fi
echo ----- dataloader status -----
ls $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status
echo ----- dataloader status end--
cp -R $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status package/

zip -r $mydirectory/buildscripts/build/artifact/$name_package package/status
# $mydirectory/buildscripts/build/jenkins_dataloader/mydataloader/target/classes/status

echo ---zip-dataloader-start---
ls  $mydirectory/buildscripts/build/artifact/
echo ----zip-dataloader-end----


rm tmp
rm temp1
echo ----end prepare dataloader----
