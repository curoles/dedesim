#!/bin/bash

THIS_SCRIPT_DIR="$(dirname ${BASH_SOURCE[0]})"
INSTALL_DIR=${THIS_SCRIPT_DIR}

scala -cp "${INSTALL_DIR}/*" curoles.dedesim.SimRunner "$@"

