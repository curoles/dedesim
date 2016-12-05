
BABY_TEST1 := \
    println(\"Baby one\"); \
    sim.run(10)

.PHONY: test
test: SIM := $(INSTALL_PATH)/dedesim
test: CLK_TB := val circuit = new curoles.dedesim.test.circuit.clk.TB(simulator.root, \"TB\")
test: BABY_TB := val circuit = new curoles.dedesim.test.circuit.baby.TB(simulator.root, \"TB\")
test: scala_test installation
	$(SIM) "$(CLK_TB); println(simulator.root.hierarchyString())"
	$(SIM) "$(CLK_TB); sim.run(10)"
	$(SIM) "$(CLK_TB); val vcd = new VCD(\"wave.vcd\");simulator.enableWireEvent();sim.run(10); vcd.close()"
	$(SIM) "$(BABY_TB); $(BABY_TEST1)"
