ifndef SCALAC
$(error SCALAC is not defined)
endif

ifndef FASTSCALAC
$(error FASTSCALAC is not defined)
endif

#SCALAC_FLAGS := -cp "$(SCALA_CLASS_DIR):$(SCALA_EXTRA_LIB_DIR)/*"
SCALAC_FLAGS := -cp "$(SCALA_CLASS_DIR)"
SCALAC_FLAGS += -Xlint -Xfatal-warnings

THE_SCALAC := $(FASTSCALAC)

$(SCALA_CUROLES_CLASS_DIR)/%.class : $(SCALA_SRC_DIR)/%.scala
	@echo Compiling $@
	@$(THE_SCALAC) $(SCALAC_FLAGS) -d $(SCALA_CLASS_DIR) $<

