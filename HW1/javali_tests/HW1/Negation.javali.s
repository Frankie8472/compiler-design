  # Emitting class Main {...}
    # Emitting void main(...) {...}
    .globl main
    .section .data
label_int:
    .string "%d"
label_new_line:
    .string "\n"
    .section .data
var_A:
    .int 0
var_B:
    .int 0
var_a:
    .int 0
var_b:
    .int 0
var_c:
    .int 0
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
          #START
          #1
          #SECOND
          #0
          #END
            # Emitting -(B)
              # Emitting B
              movl var_B, %edi
            negl %edi
            # Emitting A
            movl var_A, %esi
          imull %edi, %esi
        movl %esi, var_a
        # Emitting b = (-(A) * B)
          # Emitting (-(A) * B)
          #START
          #0
          #SECOND
          #1
          #END
            # Emitting -(A)
              # Emitting A
              movl var_A, %esi
            negl %esi
            # Emitting B
            movl var_B, %edi
          imull %edi, %esi
        movl %esi, var_b
        # Emitting c = -((A + B))
          # Emitting -((A + B))
            # Emitting (A + B)
            #START
            #0
            #SECOND
            #0
            #END
              # Emitting A
              movl var_A, %esi
              # Emitting B
              movl var_B, %edi
            addl %edi, %esi
          negl %esi
        movl %esi, var_c
        # Emitting d = -((A * B))
          # Emitting -((A * B))
            # Emitting (A * B)
            #START
            #0
            #SECOND
            #0
            #END
              # Emitting A
              movl var_A, %esi
              # Emitting B
              movl var_B, %edi
            imull %edi, %esi
          negl %esi
        movl %esi, var_d
        # Emitting write(a)
          # Emitting a
          movl var_a, %esi
        push %esi
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
        # Emitting write(b)
          # Emitting b
          movl var_b, %esi
        push %esi
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
        # Emitting write(c)
          # Emitting c
          movl var_c, %esi
        push %esi
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
        # Emitting write(d)
          # Emitting d
          movl var_d, %esi
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
