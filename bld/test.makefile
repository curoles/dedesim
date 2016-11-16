
.PHONY: test
test: SIM := $(INSTALL_PATH)/dedesim
test: scala_test installation
	$(SIM) "val circuit = new curoles.dedesim.test.circuit.clk.TB; sim.run(10)"
