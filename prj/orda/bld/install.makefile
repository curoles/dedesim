.PHONY: installation
installation: install_jars install_scripts

.PHONY: install_jars
install_jars: $(INSTALL_PATH)/orda_dsn.jar $(INSTALL_PATH)/orda_dv.jar

$(INSTALL_PATH)/orda_dv.jar: $(INSTALL_PATH) $(SCALA_DV_OBJS) scala_dv
	jar cvf $@ -C $(SCALA_CLASS_DIR) curoles/orda/dv

$(INSTALL_PATH)/orda_dsn.jar: $(INSTALL_PATH) $(SCALA_DSN_OBJS) scala_dsn
	jar cvf $@ -C $(SCALA_CLASS_DIR) curoles/orda/design



.PHONY: install_scripts
install_scripts: $(INSTALL_PATH)/dedesim $(INSTALL_PATH)/HealthCheck.scala


$(INSTALL_PATH)/dedesim: $(INSTALL_PATH) $(SRC_BLD)/dedesim.bash
	@echo Installing $@
	sed -e 's#scala#$(SCALA)#g' $(SRC_BLD)/dedesim.bash > $@
	chmod a+x $@

$(INSTALL_PATH)/HealthCheck.scala: $(INSTALL_PATH) $(SRC_BLD)/HealthCheck.scala
	@echo Installing $@
	cp $(SRC_BLD)/HealthCheck.scala $@

