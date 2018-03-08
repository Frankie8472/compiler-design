  # Emitting class Main {...}
    # Emitting void main(...) {...}
    .globl main
    .section .data
label_int:
    .string "%d"
label_new_line:
    .string "\n"
    .section .data
      # Emitting int A
var_A:
      .int 0
      # Emitting int B
var_B:
      .int 0
      # Emitting int a
var_a:
      .int 0
      # Emitting int b
var_b:
      .int 0
      # Emitting int c
var_c:
      .int 0
      # Emitting int d
var_d:
      .int 0
    .section .text
main:
    push %ebp
    movl %esp, %ebp
      # Emitting (...)
        # Emitting A = 1
          # Emitting 1
          movl $1, %edi
        movl %edi, var_A
        # Emitting B = 1
          # Emitting 1
          movl $1, %edi
        movl %edi, var_B
        # Emitting a = (A * -(B))
          # Emitting (A * -(B))
          #LEFT
          #1
          #RIGHT
          #1
          #END
            # Emitting A
            movl var_A, %edi
            # Emitting -(B)
              # Emitting B
              movl var_B, %esi
            negl %esi
          imull %esi, %edi
        movl %edi, var_a
        # Emitting b = (-(A) * B)
          # Emitting (-(A) * B)
          #LEFT
          #1
          #RIGHT
          #1
          #END
            # Emitting -(A)
              # Emitting A
              movl var_A, %edi
            negl %edi
            # Emitting B
            movl var_B, %esi
          imull %esi, %edi
        movl %edi, var_b
        # Emitting c = -((A + B))
          # Emitting -((A + B))
            # Emitting (A + B)
            #LEFT
            #1
            #RIGHT
            #1
            #END
              # Emitting A
              movl var_A, %edi
              # Emitting B
              movl var_B, %esi
            addl %esi, %edi
          negl %edi
        movl %edi, var_c
        # Emitting d = -((A * B))
          # Emitting -((A * B))
            # Emitting (A * B)
            #LEFT
            #1
            #RIGHT
            #1
            #END
              # Emitting A
              movl var_A, %edi
              # Emitting B
              movl var_B, %esi
            imull %esi, %edi
          negl %edi
        movl %edi, var_d
        # Emitting write(a)
          # Emitting a
          movl var_a, %edi
        push %edi
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
        # Emitting write(b)
          # Emitting b
          movl var_b, %edi
        push %edi
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
        # Emitting write(c)
          # Emitting c
          movl var_c, %edi
        push %edi
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
        # Emitting write(d)
          # Emitting d
          movl var_d, %edi
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
