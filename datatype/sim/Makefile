tb_name = Test
timeout_cycles = 30

verilator_bin = verilator

root_dir = $(abspath ..)
vout_dir = $(abspath .)/vout
csrc_dir = $(resources_dir)/csrc
resources_dir = $(abspath ..)/src/test/resources

vsrc_files = $(vout_dir)/$(tb_name).v
csrc_files = $(resources_dir)/csrc/main.cc

vcd = $(vout_dir)/$(tb_name).vcd

vbin = $(vout_dir)/$(tb_name)
vbin_debug = $(vout_dir)/$(tb_name)-debug

CFLAGS = -I$(csrc_dir) -include V$(tb_name).h -DVNAME=V$(tb_name)

default: run

run: $(vbin)
	$< --max-cycles=$(timeout_cycles)

run_debug: $(vcd)

$(vcd): $(vbin_debug)
	$< --max-cycles=$(timeout_cycles) --vcd=$@ --dump-start=0

VERILATOR_FLAGS := --cc --exe \
                   -Mdir $(vout_dir) \
		   --top-module $(tb_name) \
		   $(vsrc_files) $(csrc_files) \
		   -Wno-STMTDLY -Wno-WIDTH -Wno-CASEINCOMPLETE \
		   --output-split 20000 \
		   --output-split-cfuncs 20000 \
		   --x-assign unique \
		   +define+RANDOMIZE_GARBAGE_ASSIGN \
		   +define+RANDOMIZE_REG_INIT \
		   +define+RANDOMIZE_MEM_INIT

$(vbin): | $(vsrc_files)
	$(verilator_bin) $(VERILATOR_FLAGS) \
	-CFLAGS "$(CFLAGS)" \
	-o $@
	make VM_PARALLEL_BUILDS=1 -C $(vout_dir) -f V$(tb_name).mk

$(vbin_debug): | $(vsrc_files)
	$(verilator_bin) $(VERILATOR_FLAGS) --trace \
	-CFLAGS "$(CFLAGS)" \
	-o $@
	make VM_PARALLEL_BUILDS=1 -C $(vout_dir) -f V$(tb_name).mk

$(vsrc_files): | $(vout_dir)
	cd $(root_dir) && sbt 'test:runMain example.test.Elaborate --target-dir $(vout_dir) --top-name $(tb_name)'

$(vout_dir):
	mkdir -p $@

clean:
	-rm -rf $(vout_dir)
