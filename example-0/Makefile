work_dir = $(abspath .)

default: verilog

verilog:
	sbt 'runMain example.Foo --target-dir generated'

test:
	sbt 'test:runMain example.test.Elaborate --target-dir testbench'

clean:
	-rm -rf generated testbench target
