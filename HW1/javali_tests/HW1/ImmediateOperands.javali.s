  # Emitting class Main {...}
    # Emitting void main(...) {...}
    .globl main
    .section .data
label_int:
    .string "%d"
label_new_line:
    .string "\n"
    .section .data
      # Emitting int i0
var_i0:
      .int 0
    .section .text
main:
    push %ebp
    movl %esp, %ebp
      # Emitting (...)
        # Emitting i0 = 0
          # Emitting 0
          movl $0, %edi
        movl %edi, var_i0
        # Emitting i0 = (5 + i0)
          # Emitting (5 + i0)
            # Emitting i0
            movl var_i0, %edi
            # Emitting 5
            movl $5, %esi
          addl %edi, %esi
        movl %esi, var_i0
        # Emitting write(i0)
          # Emitting i0
          movl var_i0, %esi
        push %esi
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
        # Emitting i0 = (i0 + 5)
          # Emitting (i0 + 5)
            # Emitting 5
            movl $5, %esi
            # Emitting i0
            movl var_i0, %edi
          addl %esi, %edi
        movl %edi, var_i0
        # Emitting write(i0)
          # Emitting i0
          movl var_i0, %edi
        push %edi
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
        # Emitting i0 = ((i0 + 5) + 3)
          # Emitting ((i0 + 5) + 3)
            # Emitting (i0 + 5)
              # Emitting 5
              movl $5, %edi
              # Emitting i0
              movl var_i0, %esi
            addl %edi, %esi
            # Emitting 3
            movl $3, %edi
          addl %edi, %esi
        movl %esi, var_i0
        # Emitting write(i0)
          # Emitting i0
          movl var_i0, %esi
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