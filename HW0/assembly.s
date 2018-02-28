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

# create new stack frame
push %ebp
mov  %esp, %ebp


# read input value and store it on the stack
subl $4, %esp   ## allocate stack
push %esp       ## push ptr for input
push $.format   ## push "%d"
call scanf      ## call to scanf

# increment value on the stack
incl 8(%esp)

# save value on second place on stack
movl 8(%esp), %ecx
movl %ecx, 4(%esp)

# print out incremented value
call printf

# deallocate stack
addl $12, %esp

# restore old stack frame
pop %ebp

# return
ret
