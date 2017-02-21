
SCALA_SRC_DIR := $(SOURCE_PATH)/src/scala
SCALA_BUILD_DIR := ./build/scala
SCALA_CLASS_DIR := $(SCALA_BUILD_DIR)/class
SCALA_CUROLES_CLASS_DIR := $(SCALA_CLASS_DIR)/curoles
SCALA_EXTRA_LIB_DIR := $(SCALA_BUILD_DIR)/extralib

SCALA_SRCs := 
SCALA_SRCs += dedesim/util/BinaryLiteral.scala
SCALA_SRCs += dedesim/util/VCDWriter.scala
SCALA_SRCs += dedesim/Version.scala
SCALA_SRCs += dedesim/De.scala
SCALA_SRCs += dedesim/Component.scala dedesim/Trigger.scala
SCALA_SRCs += dedesim/Wire.scala dedesim/FlipFlop.scala
SCALA_SRCs += dedesim/Module.scala dedesim/RootModule.scala
SCALA_SRCs += dedesim/Messenger.scala
SCALA_SRCs += dedesim/Simulation.scala dedesim/Simulator.scala
SCALA_SRCs += dedesim/Driver.scala dedesim/Basic.scala
SCALA_SRCs += dedesim/hwlib/AddrDecoder.scala
SCALA_SRCs += dedesim/VCDumper.scala
SCALA_SRCs += dedesim/SimRunner.scala

include $(BLD)/test_circuits.makefile

# Scala source files with the path.
SCALA_SRCS := $(addprefix $(SCALA_SRC_DIR)/,$(SCALA_SRCs))

SCALA_OBJs := $(SCALA_SRCs:.scala=.class)
SCALA_OBJS := $(addprefix $(SCALA_CUROLES_CLASS_DIR)/,$(SCALA_OBJs))

.PHONY: scala
scala: $(SCALA_CLASS_DIR) scala_extra_libs $(SCALA_OBJS)
	$(THE_SCALAC) $(SCALAC_FLAGS) -d $(SCALA_CLASS_DIR) $(SCALA_SRCS)

$(SCALA_CLASS_DIR):
	mkdir -p $(SCALA_CLASS_DIR)

$(SCALA_EXTRA_LIB_DIR):
	mkdir -p $(SCALA_EXTRA_LIB_DIR)

SCALATEST_VER := $(SCALA_MAJOR_VER)-3.0.1

.PHONY: scala_extra_libs
scala_extra_libs: $(SCALA_EXTRA_LIB_DIR)/scalatest_$(SCALATEST_VER).jar
scala_extra_libs: $(SCALA_EXTRA_LIB_DIR)/scalactic_$(SCALATEST_VER).jar

$(SCALA_EXTRA_LIB_DIR)/scalatest_$(SCALATEST_VER).jar:
	@mkdir -p $(SCALA_EXTRA_LIB_DIR)
	wget -P $(SCALA_EXTRA_LIB_DIR) https://oss.sonatype.org/content/groups/public/org/scalatest/scalatest_$(SCALA_MAJOR_VER)/3.0.1/scalatest_$(SCALATEST_VER).jar

$(SCALA_EXTRA_LIB_DIR)/scalactic_$(SCALATEST_VER).jar:
	@mkdir -p $(SCALA_EXTRA_LIB_DIR)
	wget -P $(SCALA_EXTRA_LIB_DIR) https://oss.sonatype.org/content/groups/public/org/scalactic/scalactic_$(SCALA_MAJOR_VER)/3.0.1/scalactic_$(SCALATEST_VER).jar

.PHONY: scala_doc
scala_doc: SCALAC_FLAGS := -cp "$(SCALA_EXTRA_LIB_DIR)/*"
scala_doc: $(SCALA_SRCS)
	mkdir -p ./build/doc/scala
	scaladoc -d ./build/doc/scala $(SCALAC_FLAGS) $(SCALA_SRCS)

.PHONY: scala_test
scala_test: classpath := "./build/scala/class:./build/scala/extralib/*"
scala_test: run_scala_test := $(SCALA) -cp $(classpath) org.scalatest.run
scala_test:
	$(run_scala_test) curoles.dedesim.TriggerSpec
	$(run_scala_test) curoles.dedesim.hwlib.AddrDecoderSpec
