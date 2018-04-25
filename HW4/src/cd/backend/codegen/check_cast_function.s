	.file	"check_casts.c"
	.text
	.globl	cast
	.type	cast, @function
cast:
.LFB10:
	.cfi_startproc
	subl	$28, %esp
	.cfi_def_cfa_offset 32
	movl	32(%esp), %edx
	movl	36(%esp), %eax
	testb	$1, %al
	jne	.L2
	testl	%eax, %eax
	je	.L3
	cmpl	%edx, %eax
	jne	.L5
	.p2align 4,,7
	jmp	.L1
.L2:
	cmpl	%edx, %eax
	.p2align 4,,7
	je	.L1
	cmpl	{0}, %edx
	.p2align 4,,3
	je	.L1
	movl	$1, (%esp)
	call	exit
.L6:
	cmpl	%eax, %edx
	je	.L1
.L5:
	movl	(%eax), %eax
	testl	%eax, %eax
	jne	.L6
.L3:
	movl	$1, (%esp)
	call	{1}
.L1:
	addl	$28, %esp
	.cfi_def_cfa_offset 4
	ret
	.cfi_endproc
.LFE10:
	.size	cast, .-cast
	.ident	"GCC: (SUSE Linux) 4.8.5"
	.section	.note.GNU-stack,"",@progbits
