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
      # Emitting int r3
var_r3:
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
      # Emitting int i3
var_i3:
      .int 0
      # Emitting int i4
var_i4:
      .int 0
      # Emitting int i5
var_i5:
      .int 0
      # Emitting int i6
var_i6:
      .int 0
      # Emitting int i7
var_i7:
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
        # Emitting i1 = 1
          # Emitting 1
          movl $1, %edi
        movl %edi, var_i1
        # Emitting i2 = 2
          # Emitting 2
          movl $2, %edi
        movl %edi, var_i2
        # Emitting i3 = 3
          # Emitting 3
          movl $3, %edi
        movl %edi, var_i3
        # Emitting i4 = 4
          # Emitting 4
          movl $4, %edi
        movl %edi, var_i4
        # Emitting i5 = 5
          # Emitting 5
          movl $5, %edi
        movl %edi, var_i5
        # Emitting i6 = 6
          # Emitting 6
          movl $6, %edi
        movl %edi, var_i6
        # Emitting i7 = 7
          # Emitting 7
          movl $7, %edi
        movl %edi, var_i7
        # Emitting r1 = (i0 + (i1 + (i2 + (i3 + (i4 + (i5 + (i6 + i7)))))))
          # Emitting (i0 + (i1 + (i2 + (i3 + (i4 + (i5 + (i6 + i7)))))))
            # Emitting (i1 + (i2 + (i3 + (i4 + (i5 + (i6 + i7))))))
              # Emitting (i2 + (i3 + (i4 + (i5 + (i6 + i7)))))
                # Emitting (i3 + (i4 + (i5 + (i6 + i7))))
                  # Emitting (i4 + (i5 + (i6 + i7)))
                    # Emitting (i5 + (i6 + i7))
                      # Emitting (i6 + i7)
                        # Emitting i7
                        movl var_i7, %edi
                        # Emitting i6
                        movl var_i6, %esi
                      addl %edi, %esi
                      # Emitting i5
                      movl var_i5, %edi
                    addl %esi, %edi
                    # Emitting i4
                    movl var_i4, %esi
                  addl %edi, %esi
                  # Emitting i3
                  movl var_i3, %edi
                addl %esi, %edi
                # Emitting i2
                movl var_i2, %esi
              addl %edi, %esi
              # Emitting i1
              movl var_i1, %edi
            addl %esi, %edi
            # Emitting i0
            movl var_i0, %esi
          addl %edi, %esi
        movl %esi, var_r1
        # Emitting r2 = (((((((i0 + i1) + i2) + i3) + i4) + i5) + i6) + i7)
          # Emitting (((((((i0 + i1) + i2) + i3) + i4) + i5) + i6) + i7)
            # Emitting ((((((i0 + i1) + i2) + i3) + i4) + i5) + i6)
              # Emitting (((((i0 + i1) + i2) + i3) + i4) + i5)
                # Emitting ((((i0 + i1) + i2) + i3) + i4)
                  # Emitting (((i0 + i1) + i2) + i3)
                    # Emitting ((i0 + i1) + i2)
                      # Emitting (i0 + i1)
                        # Emitting i1
                        movl var_i1, %esi
                        # Emitting i0
                        movl var_i0, %edi
                      addl %esi, %edi
                      # Emitting i2
                      movl var_i2, %esi
                    addl %esi, %edi
                    # Emitting i3
                    movl var_i3, %esi
                  addl %esi, %edi
                  # Emitting i4
                  movl var_i4, %esi
                addl %esi, %edi
                # Emitting i5
                movl var_i5, %esi
              addl %esi, %edi
              # Emitting i6
              movl var_i6, %esi
            addl %esi, %edi
            # Emitting i7
            movl var_i7, %esi
          addl %esi, %edi
        movl %edi, var_r2
        # Emitting r3 = (((i0 + i1) + (i2 + i3)) + ((i4 + i5) + (i6 + i7)))
          # Emitting (((i0 + i1) + (i2 + i3)) + ((i4 + i5) + (i6 + i7)))
            # Emitting ((i4 + i5) + (i6 + i7))
              # Emitting (i6 + i7)
                # Emitting i7
                movl var_i7, %edi
                # Emitting i6
                movl var_i6, %esi
              addl %edi, %esi
              # Emitting (i4 + i5)
                # Emitting i5
                movl var_i5, %edi
                # Emitting i4
                movl var_i4, %edx
              addl %edi, %edx
            addl %esi, %edx
            # Emitting ((i0 + i1) + (i2 + i3))
              # Emitting (i2 + i3)
                # Emitting i3
                movl var_i3, %esi
                # Emitting i2
                movl var_i2, %edi
              addl %esi, %edi
              # Emitting (i0 + i1)
                # Emitting i1
                movl var_i1, %esi
                # Emitting i0
                movl var_i0, %ecx
              addl %esi, %ecx
            addl %edi, %ecx
          addl %edx, %ecx
        movl %ecx, var_r3
        # Emitting write(r1)
          # Emitting r1
          movl var_r1, %ecx
        push %ecx
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
        # Emitting write(r2)
          # Emitting r2
          movl var_r2, %ecx
        push %ecx
        push $label_int
        call printf
        addl $8, %esp
        # Emitting writeln()
        push $label_new_line
        call printf
        addl $4, %esp
        # Emitting write(r3)
          # Emitting r3
          movl var_r3, %ecx
        push %ecx
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
