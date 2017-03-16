SCALA_DSN_SRCs := 
SCALA_DSN_SRCs += orda/design/OrdaCPU.scala

# Scala Design source files with the path.
SCALA_DSN_SRCS := $(addprefix $(SCALA_SRC_DIR)/,$(SCALA_DSN_SRCs))

# List of compiled .class files.
SCALA_DSN_OBJs := $(SCALA_DSN_SRCs:.scala=.class)

# Compiled files with the path.
SCALA_DSN_OBJS := $(addprefix $(SCALA_CUROLES_CLASS_DIR)/,$(SCALA_DSN_OBJs))

.PHONY: scala_dsn
scala_dsn: $(SCALA_CLASS_DIR) $(SCALA_DSN_OBJS)
	@echo Building target $@
	@$(THE_SCALAC) $(SCALAC_FLAGS) -d $(SCALA_CLASS_DIR) $(SCALA_DSN_SRCS)



