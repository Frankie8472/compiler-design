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
	cmpl	$0, 12(%ebp)
	jne	.L2
	jmp	.L1
.L2:
	jmp	.L4
.L6:
	movl	8(%ebp), %eax
	cmpl	12(%ebp), %eax
	jne	.L5
	jmp	.L1
.L5:
	movl	12(%ebp), %eax
	movl	(%eax), %eax
	movl	%eax, 12(%ebp)
.L4:
	cmpl	$0, 12(%ebp)
	jne	.L6
#APP
# 42 "check_casts.c" 1
	jmp {0}
# 0 "" 2
#NO_APP
.L1:
	popl	%ebp
	.cfi_restore 5
	.cfi_def_cfa 4, 4
	ret
	.cfi_endproc
.LFE2:
	.size	cast, .-cast
	.ident	"GCC: (SUSE Linux) 4.8.5"
	.section	.note.GNU-stack,"",@progbits
