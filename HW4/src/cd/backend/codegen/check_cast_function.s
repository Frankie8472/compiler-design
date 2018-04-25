	.file	"check_casts.c"
	.text
	.globl	cast
	.type	cast, @function
cast:
.LFB2:
	.cfi_startproc
	pushl	%ebp
	.cfi_def_cfa_offset 8
	.cfi_offset 5, -8
	movl	%esp, %ebp
	.cfi_def_cfa_register 5
	subl	$24, %esp
	movl	12(%ebp), %eax
	andl	$1, %eax
	testl	%eax, %eax
	je	.L2
	movl	12(%ebp), %eax
	cmpl	8(%ebp), %eax
	jne	.L3
	jmp	.L1
.L3:
	movl	Object_method_table, %eax
	cmpl	%eax, 8(%ebp)
	jne	.L5
	jmp	.L1
.L5:
	movl	{2}, %eax
	movl	%eax, (%esp)
	call	exit
.L2:
	jmp	.L6
.L8:
	movl	8(%ebp), %eax
	cmpl	12(%ebp), %eax
	jne	.L7
	jmp	.L1
.L7:
	movl	12(%ebp), %eax
	movl	(%eax), %eax
	movl	%eax, 12(%ebp)
.L6:
	cmpl	$0, 12(%ebp)
	jne	.L8
	movl	{2}, %eax
	movl	%eax, (%esp)
	call	exit
.L1:
	leave
	.cfi_restore 5
	.cfi_def_cfa 4, 4
	ret
	.cfi_endproc
.LFE2:
	.size	cast, .-cast
	.ident	"GCC: (SUSE Linux) 4.8.5"
	.section	.note.GNU-stack,"",@progbits
