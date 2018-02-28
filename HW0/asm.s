#-----------------------------
#   Read Number and add one
#       Josua Cantieni
#-----------------------------
.globl main

pattern:
	.string	"%d"

main:
	pushl	%ebp                # Init-call
	movl	%esp, %ebp          
    subl    $8, %esp            # Allocate Stack for one variable, one pointer to $pattern
    
    movl	$pattern, (%esp)    # Move Pointer to pattern to Stack (first argument)
    leal	4(%esp), %eax       # Store Pointer to %esp+4 in %eax
	movl	%eax, 4(%esp)       # Store %eax in Stack+4 (second argument)

    call    scanf               # Read number. store in Stack+4
    
    testl	%eax, %eax          # Test if Number was found
    movl    $1, %eax            # Set return code if a error occurs 
	je	    _main_exit          # If no number was found exit
    
    incl    4(%esp)             # Add One to read value
	call	printf              # Print read value
    
    xorl    %eax, %eax          # Returncode 0
    
 _main_exit:  
    leave
    ret
    