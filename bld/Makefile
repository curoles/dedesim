# DeDeSim makefile
#
# author: Igor Lesik
# copyright: Igor Lesik 2016

# All = installation
.PHONY: all
all: installation

EMPTY :=
SPACE := $(EMPTY) $(EMPTY)

# Path where applications are installed.
INSTALL_PATH := $(abspath ./install)

# Path to build files
BLD := $(SOURCE_PATH)/bld

include $(BLD)/java.makefile
include $(BLD)/scala.makefile
include $(BLD)/rules.makefile
include $(BLD)/doc.makefile
include $(BLD)/test.makefile


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
$(INSTALL_PATH)/dedesim.jar: GET_RELEASE := git -C $(SOURCE_PATH) rev-list HEAD --count
$(INSTALL_PATH)/dedesim.jar: $(INSTALL_PATH) $(SCALA_OBJS)
	$(GET_RELEASE) > $(SCALA_CUROLES_CLASS_DIR)/dedesim/release.txt
	jar cvf $@ -C $(SCALA_CLASS_DIR) .
	zip -d $@ curoles/dedesim/test/*

$(INSTALL_PATH)/dedesim_test_circuits.jar: $(INSTALL_PATH) $(SCALA_OBJS)
	jar cvf $@ -C $(SCALA_CLASS_DIR) curoles/dedesim/test/circuit 

.PHONY: install_jars
install_jars: $(INSTALL_PATH)/dedesim.jar $(INSTALL_PATH)/dedesim_test_circuits.jar
	cp $(SCALA_EXTRA_LIB_DIR)/*.jar $(INSTALL_PATH)/.

$(INSTALL_PATH)/dedesim: $(BLD)/dedesim.bash
#	cp $(BLD)/dedesim.bash $@
	sed -e 's#scala#$(SCALA)#g' $(BLD)/dedesim.bash > $@
	chmod a+x $@

.PHONY: install_scripts
install_scripts: $(INSTALL_PATH)/dedesim
#install_scripts: $(INSTALL_PATH)/run_server.bash $(INSTALL_PATH)/run_terminal_client.bash
