	.section	.rodata
pattern:
	.string	"%d"
    .text
    .globl	main
main:
	pushl	%ebp                # Init-call
	movl	%esp, %ebp          
    subl    $8, %esp            # Allocate Stack for one variable, one pointer to $pattern
    
    leal	4(%esp), %eax       # Store Pointer to %esp+4 in %eax
	movl	%eax, 4(%esp)       # Store %eax in Stack+4 (second argument)
    movl	$pattern, (%esp)    # Move Pointer to pattern to Stack (first argument)
    call    scanf               # Read number. store in Stack+4
    incl    4(%esp)             # Add One to read value
	call	printf              # Print read value
    
    xorl    %eax, %eax          # Cleanup
    addl    $8,   %esp
    movl    %ebp, %esp
    popl     %ebp
    ret
    