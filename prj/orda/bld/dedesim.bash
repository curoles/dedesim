#!/bin/bash

THIS_SCRIPT_DIR="$(dirname ${BASH_SOURCE[0]})"
INSTALL_DIR=${THIS_SCRIPT_DIR}
DEDESIM_INSTALL_DIR=${THIS_SCRIPT_DIR}/../../build/install

#echo DeDeSim libraries:
#ls ${DEDESIM_INSTALL_DIR}

scala -cp "${INSTALL_DIR}/*:${DEDESIM_INSTALL_DIR}/*" curoles.dedesim.SimRunner "$@"

