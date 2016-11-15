# DeDeSim makefile
#
# author: Igor Lesik
# copyright: Igor Lesik 2016

# All = installation
.PHONY: all
all: installation

EMPTY :=
SPACE := $(EMPTY) $(EMPTY)

# Path to build files
BLD := $(SOURCE_PATH)/bld

include $(BLD)/java.makefile
include $(BLD)/scala.makefile
include $(BLD)/rules.makefile
include $(BLD)/doc.makefile

# Path where applications are installed.
INSTALL_PATH := ./install

# Target creates installation directory.
$(INSTALL_PATH):
	mkdir -p $(INSTALL_PATH)

# Builds all components
.PHONY: build_all
#build_all: java scala
build_all: scala

# Install all
.PHONY: installation
installation: build_all install_jars install_scripts


# A phony target should not be a prerequisite of a real target file;
# if it is, its recipe will be run every time make goes to update that file.
$(INSTALL_PATH)/dedesim.jar: $(INSTALL_PATH) $(SCALA_OBJS)
	jar cvf $@ -C $(SCALA_CLASS_DIR) . 

.PHONY: install_jars
install_jars: $(INSTALL_PATH)/dedesim.jar
	cp $(SCALA_EXTRA_LIB_DIR)/*.jar $(INSTALL_PATH)/.

$(INSTALL_PATH)/dedesim: $(BLD)/dedesim.bash
	cp $(BLD)/dedesim.bash $@
	chmod a+x $@

.PHONY: install_scripts
install_scripts: $(INSTALL_PATH)/dedesim
#install_scripts: $(INSTALL_PATH)/run_server.bash $(INSTALL_PATH)/run_terminal_client.bash
