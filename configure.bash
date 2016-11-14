# Call this script from a build directory.
# It creates proxy Makefile that inlcudes Makefile from the source directory.
#

echo Build environment configuration BASH script.

CWD=`pwd`
echo Current working directory=$CWD

SOURCE_PATH="$(dirname ${BASH_SOURCE[0]})"
echo Source path=$SOURCE_PATH

ABS_SOURCE_PATH=$(realpath $SOURCE_PATH)
echo Absolute Source path=$ABS_SOURCE_PATH

#SETUP_SHELL_BASH="setup_shell.bash"
#touch $SETUP_SHELL_BASH

MAKE_CONFIG="config.makefile"
touch $MAKE_CONFIG
echo "# Autogenerated MAKE configuration" > $MAKE_CONFIG
echo "SOURCE_PATH:=$SOURCE_PATH" >> $MAKE_CONFIG

MAKEFILE="Makefile"
touch $MAKEFILE

echo "include $MAKE_CONFIG" > $MAKEFILE
echo "-include custom.config.makefile" >> $MAKEFILE
echo "include $SOURCE_PATH/bld/Makefile" >> $MAKEFILE

#echo "SOURCE_PATH=$SOURCE_PATH" > $SETUP_SHELL_BASH

JAVAC_PATH=`which javac`
echo JAVAC=$JAVAC_PATH
echo "JAVAC:=$JAVAC_PATH" >> $MAKE_CONFIG

SCALAC_PATH=`which scalac`
echo SCALAC=$SCALAC_PATH
echo "SCALAC:=$SCALAC_PATH" >> $MAKE_CONFIG

FASTSCALAC_PATH=`which fsc`
echo FASTSCALAC=$FASTSCALAC_PATH
echo "FASTSCALAC:=$FASTSCALAC_PATH" >> $MAKE_CONFIG

