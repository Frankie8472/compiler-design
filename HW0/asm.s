#-----------------------------
#   Read Number and add one
#       Josua Cantieni
#-----------------------------
.globl main

.section .rodata                    # Preinitialized data
pattern_scan:
    .string "%d"                    # Pattern for scanf
pattern_print:
    .string "%d\n"                  # Pattern for printf
    
.text
main:
    pushl   %ebp                    # Init-call
    movl    %esp, %ebp          
    subl    $4, %esp                # Allocate Stack for one variable, one pointer to $pattern
    
    movl    %esp, (%esp)
    pushl   $pattern_scan  #, (%esp)   # Move Pointer to pattern to Stack (first argument)
    # leal    4(%esp), %eax           # Store Pointer to %esp+4 in %eax
    # movl    %eax, 4(%esp)           # Store %eax in Stack+4 (second argument)
    
    call    scanf                   # Read number. store in Stack+4
    
    testl   %eax, %eax              # Test if Number was found
    movl    $1, %eax                # Set return code if a error occurs 
    je      .L0                     # If no number was found exit
    
    incl    4(%esp)                 # Add One to read value
    movl    $pattern_print, (%esp) 
    call    printf                  # Print read value
    
    xorl    %eax, %eax              # Returncode 0
    
.L0:  
    leave                           # Cleanup
    ret
    