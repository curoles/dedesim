
HOSTNAME := $(shell hostname)

$(info Host name is '$(HOSTNAME)')

ifeq ($(HOSTNAME), pc104.smi.local)
  #$(info SMI machine)
  SCALA_HOME:=/local_disk/igor/tools/scala/scala-2.12.1
  JAVA_HOME:=/local_disk/igor/tools/java/java8/jdk1.8.0_121
  JAVAC:=$(JAVA_HOME)/bin/javac
  SCALA:=JAVA_HOME=$(JAVA_HOME) $(SCALA_HOME)/bin/scala
  SCALAC:=JAVA_HOME=$(JAVA_HOME) $(SCALA_HOME)/bin/scalac
  FASTSCALAC:=JAVA_HOME=$(JAVA_HOME) $(SCALA_HOME)/bin/fsc
  SCALADOC:=JAVA_HOME=$(JAVA_HOME) $(SCALA_HOME)/bin/scaladoc
  DOXYGEN:=/tools/local/doxygen-1.8.11/bin/doxygen
  DITAA:=/local_disk/igor/tools/ditaa/ditaa
else
 #/home/igor/tool/ditaa/ditaa
endif 
