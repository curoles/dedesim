
.PHONY: test
test: SIM := $(INSTALL_PATH)/dedesim
test: NEW_TB := val circuit = new curoles.dedesim.test.circuit.clk.TB(simulator.root, \"TB\")
test: scala_test installation
	$(SIM) "$(NEW_TB); println(simulator.root.hierarchyString())"
	$(SIM) "$(NEW_TB); sim.run(10)"
	$(SIM) "$(NEW_TB); val vcd = new VCD(\"wave.vcd\");sim.run(10); vcd.close()"
