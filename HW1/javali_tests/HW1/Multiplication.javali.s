  # Emitting class Main {...}
    # Emitting void main(...) {...}
    .globl main
    .section .data
label_int:
    .string "%d"
label_new_line:
    .string "\n"
    .section .data
      # Emitting int r1
var_r1:
      .int 0
      # Emitting int i0
var_i0:
      .int 0
      # Emitting int i1
var_i1:
      .int 0
    .section .text
main:
    push %ebp
    movl %esp, %ebp
      # Emitting (...)
        # Emitting i0 = 5
          # Emitting 5
          movl $5, %edi
        movl %edi, var_i0
        # Emitting i1 = 2
          # Emitting 2
          movl $2, %edi
        movl %edi, var_i1
        # Emitting r1 = (i1 * 3)
          # Emitting (i1 * 3)
            # Emitting 3
            movl $3, %edi
            # Emitting i1
            movl var_i1, %esi
          imull %edi, %esi
        movl %esi, var_r1
        # Emitting write(r1)
          # Emitting r1
          movl var_r1, %esi
        push %esi
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
        # Emitting r1 = (i0 * i1)
          # Emitting (i0 * i1)
            # Emitting i1
            movl var_i1, %esi
            # Emitting i0
            movl var_i0, %edi
          imull %esi, %edi
        movl %edi, var_r1
        # Emitting write(r1)
          # Emitting r1
          movl var_r1, %edi
        push %edi
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
        # Emitting r1 = (((r1 * i0) * i1) * 3)
          # Emitting (((r1 * i0) * i1) * 3)
            # Emitting ((r1 * i0) * i1)
              # Emitting (r1 * i0)
                # Emitting i0
                movl var_i0, %edi
                # Emitting r1
                movl var_r1, %esi
              imull %edi, %esi
              # Emitting i1
              movl var_i1, %edi
            imull %edi, %esi
            # Emitting 3
            movl $3, %edi
          imull %edi, %esi
        movl %esi, var_r1
        # Emitting write(r1)
          # Emitting r1
          movl var_r1, %esi
        push %esi
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
    xorl %eax, %eax
    leave
    ret
