#!/bin/bash

ideaVersion="2017.3"
if [ "$IDEA_ENV" == "eap" ]; then
    ideaVersion="163.5644.15"
else
    ideaVersion="$IDEA_ENV"
fi

travisCache=".cache"

if [ ! -d ${travisCache} ]; then
    echo "Create cache" ${travisCache} 
    mkdir ${travisCache}
fi

function download {

  url=$1
  basename=${url##*[/|\\]}
  cachefile=${travisCache}/${basename}
  
  if [ ! -f ${cachefile} ]; then
      wget $url -P ${travisCache};
    else
      echo "Cached file `ls -sh $cachefile` - `date -r $cachefile +'%Y-%m-%d %H:%M:%S'`"
  fi  

  if [ ! -f ${cachefile} ]; then
    echo "Failed to download: $url"
    exit 1
  fi  
}

# Unzip IDEA

if [ -d ./idea  ]; then
  rm -rf idea
  mkdir idea
  echo "created idea dir"  
fi

# Download main idea folder
download "http://download.jetbrains.com/idea/ideaIU-${ideaVersion}.tar.gz"
tar zxf ${travisCache}/ideaIU-${ideaVersion}.tar.gz -C .

# Move the versioned IDEA folder to a known location
ideaPath=$(find . -name 'idea-IU*' | head -n 1)
mv ${ideaPath} ./idea
  
if [ -d ./plugins ]; then
  rm -rf plugins
  mkdir plugins
  echo "created plugin dir"  
fi

if [ "$PHPSTORM_ENV" == "eap" ]; then
    #php
    PHPSTORM_URL=$(curl -sS "https://plugins.jetbrains.com/api/plugins/6610/updates?channel=&size=100" | jq '.[]|[.compatibleVersions.IDEA, .file]|select(.[0]|endswith("(eap)"))|.[1]' --raw-output)
    curl -o php.zip "https://plugins.jetbrains.com/files/$PHPSTORM_URL"
    unzip -qo php.zip -d ./plugins

elif [ "$PHPSTORM_ENV" == "2017.3.2" ]; then
    #php
    download "https://plugins.jetbrains.com/files/6610/41723/php-173.4127.29.zip"
    unzip -qo $travisCache/php-173.4127.29.zip -d ./plugins
else
    echo "Unknown PHPSTORM_ENV value: $PHPSTORM_ENV"
    exit 1
fi

# Run the tests
if [ "$1" = "-d" ]; then
    ant -d -f build-test.xml -DIDEA_HOME=./idea
else
    ant -f build-test.xml -DIDEA_HOME=./idea
fi

# Was our build successful?
stat=$?

if [ "${TRAVIS}" != true ]; then
    ant -f build-test.xml -q clean

    if [ "$1" = "-r" ]; then
        rm -rf idea
        rm -rf plugins
    fi
fi

# Return the build status
exit ${stat}
