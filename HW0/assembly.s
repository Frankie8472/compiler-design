## Program to add one to an input and print new int 

# make main globally accessible
.globl main

# read only data
.section .rodata

.format:
.string "%d"

# text section (here goes code)
.text

## start of main
main:
pushl   %ebp            # create new stack frame
movl    %esp, %ebp
movl    %esp, (%esp)
pushl   $.format
call    scanf           # read input value
incl    4(%esp)         # increment value on the stack
call    printf          # print out incremented value
leave                   # restore old stack frame
ret
