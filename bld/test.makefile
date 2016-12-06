TEST_DIR := ./test

define test_dir
    mkdir -p $(TEST_DIR)/$(1) && cd $(TEST_DIR)/$(1)
endef

BABY_TEST1 := \
    println(\"Baby one\"); \
    val vcd = new VCD(\"wave.vcd\"); \
    simulator.enableWireEvent(); \
    sim.run(10); \
    vcd.close();

.PHONY: test
test: SIM := $(INSTALL_PATH)/dedesim
test: CD := mkdir -p 
test: CLK_TB := val circuit = new curoles.dedesim.test.circuit.clk.TB(simulator.root, \"TB\")
test: BABY_TB := val circuit = new curoles.dedesim.test.circuit.baby.TB(simulator.root, \"TB\")
test: scala_test installation
	$(call test_dir,clk1) && $(SIM) "$(CLK_TB); println(simulator.root.hierarchyString())"
	$(call test_dir,clk2) && $(SIM) "$(CLK_TB); sim.run(10)"
	$(call test_dir,clk3) && $(SIM) "$(CLK_TB); val vcd = new VCD(\"wave.vcd\");simulator.enableWireEvent();sim.run(10); vcd.close()"
	$(call test_dir,baby1) && $(SIM) "$(BABY_TB); $(BABY_TEST1)"
