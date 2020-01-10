#!/bin/sh
name=sat4j
tag=ECLIPSE_3_6
version=2.2.0
tar_name=$name-$version

rm -fr $tar_name && mkdir $tar_name
pushd $tar_name

# Fetch plugins
svn co svn://svn.forge.objectweb.org/svnroot/sat4j/maven/tags/$tag .

popd
# create archive
tar -caf $tar_name.tar.xz $tar_name
