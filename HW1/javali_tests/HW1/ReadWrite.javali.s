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
      # Emitting int r2
var_r2:
      .int 0
      # Emitting int i0
var_i0:
      .int 0
      # Emitting int i1
var_i1:
      .int 0
      # Emitting int i2
var_i2:
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
        # Emitting i1 = read()
          # Emitting read()
          subl $4, %esp
          movl %esp, (%esp)
          push $label_int
          call scanf
          movl 4(%esp), %edi
          addl $8, %esp
        movl %edi, var_i1
        # Emitting r1 = (i0 + i1)
          # Emitting (i0 + i1)
          #LEFT
          #1
          #RIGHT
          #1
          #END
            # Emitting i0
            movl var_i0, %edi
            # Emitting i1
            movl var_i1, %esi
          addl %esi, %edi
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
        # Emitting write((r1 - 3))
          # Emitting (r1 - 3)
          #LEFT
          #1
          #RIGHT
          #1
          #END
            # Emitting r1
            movl var_r1, %edi
            # Emitting 3
            movl $3, %esi
          subl %esi, %edi
        push %edi
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
