  # Emitting class Main {...}
    # Emitting void main(...) {...}
    .globl main
    .section .data
label_int:
    .string "%d"
label_new_line:
    .string "\n"
    .section .data
    .section .text
main:
    push %ebp
    movl %esp, %ebp
      # Emitting (...)
        # Emitting write(53110)
          # Emitting 53110
          movl $53110, %edi
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
