#!/bin/bash

VERSION="0.1-RC2"

TOMCAT_URL="http://tweedo.com/mirror/apache/tomcat/tomcat-7/v7.0.25/bin/apache-tomcat-7.0.25.tar.gz"
TOMCAT_DIR="apache-tomcat-7.0.25"

JETTY_VERSION="8.0.4.v20111024"
JETTY_URL="http://download.eclipse.org/jetty/$JETTY_VERSION/dist/jetty-distribution-$JETTY_VERSION.tar.gz"
JETTY_DIR="jetty-distribution-$JETTY_VERSION"

MYSQL_CONNECTOR_URL="http://search.maven.org/remotecontent?filepath=mysql/mysql-connector-java/5.1.18/mysql-connector-java-5.1.18.jar"
MYSQL_FILENAME="mysql-connector-java-5.1.18.jar"

SEARCHER="searcher/target/codesearch-searcher-0.1-RC2.war"
INDEXER="indexer/target/codesearch-indexer-0.1-RC2.war"
AUTHORS="../AUTHORS"
README="../README.md"
COPYING="../COPYING"
CONFIG="../resources/codesearch_config.xml"
RELEASES_DIR="releases"

#./rebuild-plugins.sh
mvn clean install

DOCFILES="$(readlink -f $README) $(readlink -f $AUTHORS) $(readlink -f $COPYING)"
CONFIG=$(readlink -f $CONFIG)
SEARCHER=$(readlink -f $SEARCHER)
INDEXER=$(readlink -f $INDEXER)

rm -r $RELEASES_DIR
mkdir -p $RELEASES_DIR/cache


cd $RELEASES_DIR
cd cache/
echo "Downloading required files"
curl -s $MYSQL_CONNECTOR_URL > $MYSQL_FILENAME
curl -s $TOMCAT_URL > tomcat.tar.gz
#curl -s $JETTY_URL  > jetty.tar.gz
cd ..

echo "Building archive release"
ARCHIVE_NAME="codesearch-$VERSION-archives"
mkdir $ARCHIVE_NAME
cp $SEARCHER $INDEXER $CONFIG $DOCFILES $ARCHIVE_NAME
tar caf $ARCHIVE_NAME.tar.gz $ARCHIVE_NAME
rm -r $ARCHIVE_NAME

echo "Building tomcat release"
TOMCAT_BUNDLE_NAME="codesearch-$VERSION-tomcat-bundle"
mkdir $TOMCAT_BUNDLE_NAME
cd $TOMCAT_BUNDLE_NAME
tar xaf ../cache/tomcat.tar.gz -C .
rm -rf $TOMCAT_DIR/webapps/examples
rm -rf $TOMCAT_DIR/webapps/docs
cp $SEARCHER $TOMCAT_DIR/webapps/searcher.war
cp $INDEXER $TOMCAT_DIR/webapps/indexer.war
cp $CONFIG $TOMCAT_DIR/lib/
cp $DOCFILES .
cp ../cache/mysql*.jar $TOMCAT_DIR/lib/

cd ..
tar caf $TOMCAT_BUNDLE_NAME.tar.gz $TOMCAT_BUNDLE_NAME
rm -r $TOMCAT_BUNDLE_NAME


#echo "Building jetty release"
#JETTY_BUNDLE_NAME="codesearch-$VERSION-jetty-bundle"
#mkdir $JETTY_BUNDLE_NAME
#cd $JETTY_BUNDLE_NAME
#tar xaf ../cache/jetty.tar.gz -C .
#cp ../cache/*.war $JETTY_DIR/webapps/
#cp ../cache/codesearch_config.xml $JETTY_DIR/lib
#cp ../cache/AUTHORS ../cache/README ../cache/COPYING .
#cp ../cache/mysql*.jar $JETTY_DIR/lib
#
#cd ..
#tar caf $JETTY_BUNDLE_NAME.tar.bz2 $JETTY_BUNDLE_NAME
#rm -r $JETTY_BUNDLE_NAME

rm -r cache/

