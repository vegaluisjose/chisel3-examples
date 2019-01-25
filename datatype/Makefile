work_dir = $(abspath .)

default: verilog

verilog:
	sbt 'runMain example.Elaborate --target-dir generated'

clean:
	-rm -rf generated target project/target project/project
