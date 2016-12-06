

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
SCALAC_FLAGS += -Xlint -Xfatal-warnings
#SCALAC_FLAGS += -Ywarn-adapted-args -Ywarn-dead-code -Ywarn-inaccessible -Ywarn-infer-any
#SCALAC_FLAGS += -Ywarn-nullary-override -Ywarn-nullary-unit -Ywarn-numeric-widen -Ywarn-unused
#SCALAC_FLAGS += -Ywarn-unused-import -Ywarn-value-discard

THE_SCALAC := $(FASTSCALAC)

$(SCALA_CUROLES_CLASS_DIR)/%.class : $(SCALA_SRC_DIR)/%.scala
	$(THE_SCALAC) $(SCALAC_FLAGS) -d $(SCALA_CLASS_DIR) $<

