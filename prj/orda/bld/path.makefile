# Place where everything is build.
BUILD_HOME := .

# Path to the installation directory.
INSTALL_PATH := $(abspath $(BUILD_HOME)/install)

# Build root.
BUILD_DIR := $(BUILD_HOME)/build

# Documentation build directory.
DOCS_BUILD_DIR := $(BUILD_DIR)/doc

SCALA_SRC_DIR := $(SOURCE_PATH)/src/scala
SCALA_BUILD_DIR := $(BUILD_DIR)/scala
SCALA_CLASS_DIR := $(SCALA_BUILD_DIR)/class
SCALA_CUROLES_CLASS_DIR := $(SCALA_CLASS_DIR)/curoles

$(SCALA_CLASS_DIR):
	mkdir -p $(SCALA_CLASS_DIR)

$(INSTALL_PATH):
	mkdir -p $(INSTALL_PATH)

