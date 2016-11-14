#JAVA_SRC_DIR := $(SOURCE_PATH)/src/java
#JAVA_BUILD_DIR := ./build/java
#
#JAVA_SRCs := 
#JAVA_SRCs += bmb/util/ConsoleProgressBar.java
#JAVA_SRCs += bmb/net/TcpDataOutputStream.java
#JAVA_SRCs += bmb/net/MQTT.java
#JAVA_SRCs += client/ClientApp.java
#
## Java source files with the path.
#JAVA_SRCS := $(addprefix $(JAVA_SRC_DIR)/,$(JAVA_SRCs))
#
#JAVA_OBJs := $(JAVA_SRCs:.java=.class)
#JAVA_OBJS := $(addprefix $(JAVA_BUILD_DIR)/,$(JAVA_OBJs))
#
#.PHONY: java
#java: $(JAVA_BUILD_DIR) $(JAVA_OBJS)
#
#$(JAVA_BUILD_DIR):
#	mkdir -p $(JAVA_BUILD_DIR)
#
#
#$(INSTALL_PATH)/java_client.bash: $(INSTALL_PATH)
#	echo "#!/bin/bash" > $@
#	echo "CLASS_PATH=\"-cp $(JAVA_BUILD_DIR) \"" >> $@
#	echo "java \$${CLASS_PATH} client.ClientApp" >> $@
#	chmod a+x $@

