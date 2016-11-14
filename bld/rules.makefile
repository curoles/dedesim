

ifndef JAVAC
$(error JAVAC is not defined)
endif

JAVAC_FLAGS := -cp $(JAVA_BUILD_DIR) 

$(JAVA_BUILD_DIR)/%.class : $(JAVA_SRC_DIR)/%.java
	$(JAVAC) $(JAVAC_FLAGS) -d $(JAVA_BUILD_DIR) $<

ifndef SCALAC
$(error SCALAC is not defined)
endif

ifndef FASTSCALAC
$(error FASTSCALAC is not defined)
endif

SCALAC_FLAGS := -cp "$(SCALA_CLASS_DIR):$(SCALA_EXTRA_LIB_DIR)/*"
SCALAC_FLAGS += -Xlint

THE_SCALAC := $(FASTSCALAC)

$(SCALA_CUROLES_CLASS_DIR)/%.class : $(SCALA_SRC_DIR)/%.scala
	$(THE_SCALAC) $(SCALAC_FLAGS) -d $(SCALA_CLASS_DIR) $<

