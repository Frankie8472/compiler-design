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
	jmp	.L2
.L5:
	movl	8(%ebp), %eax
	cmpl	12(%ebp), %eax
	jne	.L3
	jmp	.L6
.L3:
	movl	12(%ebp), %eax
	movl	(%eax), %eax
	movl	%eax, 12(%ebp)
.L2:
	cmpl	$0, 12(%ebp)
	jne	.L5
	movl	{2}, %eax
	movl	%eax, (%esp)
	call	{1}
.L6:
	leave
	.cfi_restore 5
	.cfi_def_cfa 4, 4
	ret
	.cfi_endproc
.LFE2:
	.size	cast, .-cast
	.ident	"GCC: (SUSE Linux) 4.8.5"
	.section	.note.GNU-stack,"",@progbits
