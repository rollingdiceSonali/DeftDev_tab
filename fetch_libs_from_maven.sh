#!/bin/bash
set -x

# Artifactory location
#public maven
server=https://www.houndify.com/maven

#internal maven
#server=http://artifactory-a-1.melodis.com:8081/artifactory/simple/libs-release-local


#rem Pulls an AAR artifact from maven and copies the aar into the libs and the javadoc and source to the project root
#rem arg name
#rem arg repo
getAARArtifact() {
    artifact=$1
    repo=$2
    path=$server/$repo/$artifact
    version=`curl -s $path/maven-metadata.xml | grep latest | sed "s/.*<latest>\([^<]*\)<\/latest>.*/\1/"`
    build=`curl -s $path/$version/maven-metadata.xml | grep '<value>' | head -1 | sed "s/.*<value>\([^<]*\)<\/value>.*/\1/"`
    aar_file=$1-$version.aar
    sources=$1-$version-sources.jar
    javadoc=$1-$version-javadoc.jar

    # Download
    wget -N $path/$version/$sources
    wget -N $path/$version/$javadoc
    wget -N $path/$version/$aar_file -P ./libs
}

#rem Pulls an JAR artifact from maven and copies in into the libs directory
#rem arg name
#rem arg repo
getJARArtifact() {
    artifact=$1
    repo=$2
    path=$server/$repo/$artifact
    version=`curl -s $path/maven-metadata.xml | grep latest | sed "s/.*<latest>\([^<]*\)<\/latest>.*/\1/"`
    build=`curl -s $path/$version/maven-metadata.xml | grep '<value>' | head -1 | sed "s/.*<value>\([^<]*\)<\/value>.*/\1/"`
    jar_file=$1-$version.jar
    sources=$1-$version-sources.jar
    javadoc=$1-$version-javadoc.jar

    wget -N $path/$version/$jar_file -P ./libs
}

// Grab latest AAR and JAR libs
getAARArtifact hound-sdk        hound/android
getAARArtifact phrasespotter    hound/android
getAARArtifact speex            soundhound/android
getAARArtifact vad              soundhound/android
getJARArtifact java-utils       hound/android