tb_name = Test

testbench_dir = $(abspath ..)/testbench
resources_dir = $(abspath ..)/src/test/resources
vout_dir = $(abspath .)/vout

vsrc_files = $(testbench_dir)/Test.v
csrc_files = $(resources_dir)/csrc/main.cc

vcd_file = $(vout_dir)/$(tb_name).vcd

default: sim_debug

sim: $(vout_dir)/V$(tb_name)
	$<

sim_debug: $(vout_dir)/V$(tb_name)_debug
	$<

timeout_cycles = 800

VERILATOR_FLAGS := --cc --exe \
                   -Mdir $(vout_dir) \
		   --top-module $(tb_name) \
		   $(vsrc_files) $(csrc_files) \
		   -Wno-STMTDLY -Wno-WIDTH -Wno-CASEINCOMPLETE \
		   --output-split 20000 \
		   --output-split-cfuncs 20000 \
		   --x-assign unique

verilator_bin = verilator

$(vout_dir)/V$(tb_name): | $(testbench_dir)/Test.v
	$(verilator_bin) $(VERILATOR_FLAGS) \
	-CFLAGS "-I$(resources_dir)/csrc" \
	-CFLAGS "-include V$(tb_name).h -DVNAME=V$(tb_name) -DTIMEOUT_CYCLES=$(timeout_cycles)" \
	-o $(vout_dir)/V$(tb_name)
	make VM_PARALLEL_BUILDS=1 -C $(vout_dir) -f V$(tb_name).mk

$(vout_dir)/V$(tb_name)_debug: | $(testbench_dir)/Test.v
	$(verilator_bin) $(VERILATOR_FLAGS) --trace \
	-CFLAGS "-I$(resources_dir)/csrc" \
	-CFLAGS "-include V$(tb_name).h -DVNAME=V$(tb_name) -DDEBUG_TRACE" \
	-CFLAGS "-DVCD_FILE=$(vcd_file) -DTIMEOUT_CYCLES=$(timeout_cycles)" \
	-o $(vout_dir)/V$(tb_name)_debug
	make VM_PARALLEL_BUILDS=1 -C $(vout_dir) -f V$(tb_name).mk

$(testbench_dir)/Test.v:
	make -C ../ test

clean:
	-rm -rf $(vout_dir)
	make -C ../ clean
