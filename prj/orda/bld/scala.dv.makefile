SCALA_DV_SRCs := 
SCALA_DV_SRCs += orda/dv/TestBench.scala

# Scala DV source files with the path.
SCALA_DV_SRCS := $(addprefix $(SCALA_SRC_DIR)/,$(SCALA_DV_SRCs))

# List of compiled .class files.
SCALA_DV_OBJs := $(SCALA_DV_SRCs:.scala=.class)

# Compiled files with the path.
SCALA_DV_OBJS := $(addprefix $(SCALA_CUROLES_CLASS_DIR)/,$(SCALA_DV_OBJs))

.PHONY: scala_dv
scala_dv: $(SCALA_CLASS_DIR) $(SCALA_DV_OBJS)
	@echo Building target $@
	@$(THE_SCALAC) $(SCALAC_FLAGS) -d $(SCALA_CLASS_DIR) $(SCALA_DV_SRCS)



