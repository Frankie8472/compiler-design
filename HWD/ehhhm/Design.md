# Design


## VTable (See Class VTable)

The vtable consists of a sequence of a pointer to a class' vtable followed by pointers to the functions of that class. When a class inherits from another class, the parent's vtable is copied, overrided methods are replaced and the pointer to the vtable as well as the remaining additional methods are appended to the table.

## MemoryLayout (See Class MemLayout)

The memory layout of a class consists of a pointer to the vtable followed by the fields on that class. If a class inherits from another, the vtable pointer is still the childs but the parent's class members are added first, followed by the childs.

## StackFrame

### Parameters
Parameters in the stack frame are passed above the EBP register. The first argument is located at EBP + 8 and is always a pointer to this and additional arguments are located above with offsets of pointer size.

### Local Variables
Space for local variables is calculated at compile time, the first being located at EBP - 16 and additional ones found after it with offsets of pointer size.

### Callee Registers
The callee registers are stored on the stack, starting at EBP - 4. They are saved on function entry and restored when the function completes.

### Method Calls
The stack is allocated in such a way that there is enough space for the function parameters and caller saved registers. When a function call is made, the parameters are not pushed but placed relative to ESP to preserve stack alignment. 

### Alignment
The size is calculated statically and therefore the alignment can be as well. It is calculated so that it is a multiple of 16 with an offset of 8 so that after the return address and base pointer are pushed by the call instruction, the function enters with offset 0. Since the method calls don't push dynamically, instead just storing relative to EBP, the alignment is always maintained.

### Zeroing
The stack is zeroed out before use to make sure that null pointers are caught.
