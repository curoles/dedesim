
SCALA_SRC_DIR := $(SOURCE_PATH)/src/scala
SCALA_BUILD_DIR := ./build/scala
SCALA_CLASS_DIR := $(SCALA_BUILD_DIR)/class
SCALA_CUROLES_CLASS_DIR := $(SCALA_CLASS_DIR)/curoles
SCALA_EXTRA_LIB_DIR := $(SCALA_BUILD_DIR)/extralib

SCALA_SRCs := 
SCALA_SRCs += dedesim/Version.scala
SCALA_SRCs += dedesim/De.scala
SCALA_SRCs += dedesim/Simulation.scala dedesim/Simulator.scala
SCALA_SRCs += dedesim/Trigger.scala
SCALA_SRCs += dedesim/Wire.scala dedesim/Driver.scala
SCALA_SRCs += dedesim/Circuit1.scala dedesim/SimRunner.scala

# Scala source files with the path.
SCALA_SRCS := $(addprefix $(SCALA_SRC_DIR)/,$(SCALA_SRCs))

SCALA_OBJs := $(SCALA_SRCs:.scala=.class)
SCALA_OBJS := $(addprefix $(SCALA_CUROLES_CLASS_DIR)/,$(SCALA_OBJs))

.PHONY: scala
scala: $(SCALA_CLASS_DIR) scala_extra_libs $(SCALA_OBJS)

$(SCALA_CLASS_DIR):
	mkdir -p $(SCALA_CLASS_DIR)

$(SCALA_EXTRA_LIB_DIR):
	mkdir -p $(SCALA_EXTRA_LIB_DIR)

.PHONY: scala_extra_libs
scala_extra_libs: $(SCALA_EXTRA_LIB_DIR)/scalatest_2.11-3.0.0.jar
scala_extra_libs: $(SCALA_EXTRA_LIB_DIR)/scalactic_2.11-3.0.0.jar

$(SCALA_EXTRA_LIB_DIR)/scalatest_2.11-3.0.0.jar:
	@mkdir -p $(SCALA_EXTRA_LIB_DIR)
	wget -P $(SCALA_EXTRA_LIB_DIR) https://oss.sonatype.org/content/groups/public/org/scalatest/scalatest_2.11/3.0.0/scalatest_2.11-3.0.0.jar

$(SCALA_EXTRA_LIB_DIR)/scalactic_2.11-3.0.0.jar:
	@mkdir -p $(SCALA_EXTRA_LIB_DIR)
	wget -P $(SCALA_EXTRA_LIB_DIR) https://oss.sonatype.org/content/groups/public/org/scalactic/scalactic_2.11/3.0.0/scalactic_2.11-3.0.0.jar

.PHONY: scala_doc
scala_doc: SCALAC_FLAGS := -cp "$(SCALA_EXTRA_LIB_DIR)/*"
scala_doc: $(SCALA_SRCS)
	mkdir -p ./build/doc/scala
	scaladoc -d ./build/doc/scala $(SCALAC_FLAGS) $(SCALA_SRCS)

.PHONY: scala_test
scala_test: classpath := "./build/scala/class:./build/scala/extralib/*"
scala_test: run_scala_test := scala -cp $(classpath) org.scalatest.run
scala_test:
	$(run_scala_test) curoles.dedesim.TriggerSpec
