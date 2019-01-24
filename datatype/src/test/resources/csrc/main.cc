#include <stdint.h>
#include <stdio.h>
#include <svdpi.h>
#include "verilated.h"

#if VM_TRACE
#include <verilated_vcd_c.h>
#endif

#if VM_TRACE
#define STRINGIZE(x) #x
#define STRINGIZE_VALUE_OF(x) STRINGIZE(x)
#endif

using namespace std;

static uint64_t trace_count = 0;

int main(int argc, char **argv) {

  Verilated::commandArgs(argc, argv);
  VNAME *top = new VNAME;

#if VM_TRACE
  Verilated::traceEverOn(true);
  VerilatedVcdC* tfp = new VerilatedVcdC;
  top->trace (tfp, 99);
  tfp->open(STRINGIZE_VALUE_OF(VCD_FILE));
#endif

  top->clock = 0;
  top->reset = 1; // assert reset on start

  for (int i = 0; i < 10; i++) {
    top->reset = 1;
    top->clock = 0;
    top->eval();
#if VM_TRACE
      tfp->dump(static_cast<vluint64_t>(trace_count * 2));
#endif
    top->clock = 1;
    top->eval();
#if VM_TRACE
      tfp->dump(static_cast<vluint64_t>(trace_count * 2 + 1));
#endif
    trace_count++;
  }
  top->reset = 0;

  while (!Verilated::gotFinish() && trace_count < TIMEOUT_CYCLES) {

    top->clock = 0;
    top->eval();

#if VM_TRACE
    tfp->dump(static_cast<vluint64_t>(trace_count * 2));
#endif

    top->clock = 1;
    top->eval();

#if VM_TRACE
    tfp->dump(static_cast<vluint64_t>(trace_count * 2 + 1));
#endif

    trace_count++;
  }

#if VM_TRACE
  if (tfp) tfp->close();
#endif
  delete top;

  exit(0);

}
