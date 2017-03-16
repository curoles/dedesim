.PHONY: install_jars
install_jars: $(INSTALL_PATH)/orda_dv.jar

$(INSTALL_PATH)/orda_dv.jar: $(INSTALL_PATH) $(SCALA_DV_OBJS)
	jar cvf $@ -C $(SCALA_CLASS_DIR) curoles/orda/dv




.PHONY: install_scripts
install_scripts: $(INSTALL_PATH)/dedesim $(INSTALL_PATH)/HealthCheck.scala


$(INSTALL_PATH)/dedesim: $(INSTALL_PATH) $(SRC_BLD)/dedesim.bash
	@echo Installing $@
	sed -e 's#scala#$(SCALA)#g' $(SRC_BLD)/dedesim.bash > $@
	chmod a+x $@

$(INSTALL_PATH)/HealthCheck.scala: $(INSTALL_PATH) $(SRC_BLD)/HealthCheck.scala
	@echo Installing $@
	cp $(SRC_BLD)/HealthCheck.scala $@

